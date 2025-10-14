import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:injectable/injectable.dart';

import '../services/storage_service.dart';
import 'injection.config.dart';

final getIt = GetIt.instance;

@InjectableInit()
Future<void> configureDependencies() async {
  getIt.init();

  // Services externes
  getIt.registerLazySingleton<Connectivity>(Connectivity.new);
  getIt.registerLazySingleton<Dio>(Dio.new);

  // Services de l'application
  getIt.registerLazySingleton<StorageService>(StorageService.new);

  // Configuration de Dio
  final dio = getIt<Dio>();
  dio.options.connectTimeout = const Duration(seconds: 30);
  dio.options.receiveTimeout = const Duration(seconds: 30);
  dio.options.sendTimeout = const Duration(seconds: 30);
  
  // Intercepteurs pour la gestion offline
  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        // Vérifier la connectivité avant d'envoyer une requête
        final connectivity = getIt<Connectivity>();
        final result = await connectivity.checkConnectivity();
        
        if (result == ConnectivityResult.none) {
          // Mode offline - stocker la requête pour plus tard
          await _storeOfflineRequest(options);
          handler.reject(
            DioException(
              requestOptions: options,
              error: 'Pas de connexion internet',
            ),
          );
        } else {
          handler.next(options);
        }
      },
      onError: (error, handler) async {
        if (error.type == DioExceptionType.connectionTimeout ||
            error.type == DioExceptionType.receiveTimeout ||
            error.type == DioExceptionType.sendTimeout) {
          // Gérer les timeouts
          handler.reject(
            DioException(
              requestOptions: error.requestOptions,
              error: 'Délai d\'attente dépassé',
            ),
          );
        } else {
          handler.next(error);
        }
      },
    ),
  );
}

// Stocker les requêtes offline pour les traiter plus tard
Future<void> _storeOfflineRequest(RequestOptions options) async {
  final storageService = getIt<StorageService>();
  final offlineRequests = storageService.getData<List<Map<String, dynamic>>>(
    'offline_requests',
        (json) => List<Map<String, dynamic>>.from(json['requests'] as List),
      ) ?? [];
  
  offlineRequests.add({
    'method': options.method,
    'url': options.path,
    'data': options.data,
    'headers': options.headers,
    'timestamp': DateTime.now().millisecondsSinceEpoch,
  });
  
  await storageService.storeData('offline_requests', {'requests': offlineRequests});
}

// Traiter les requêtes offline quand la connexion est rétablie
Future<void> processOfflineRequests() async {
  final storageService = getIt<StorageService>();
  final dio = getIt<Dio>();
  
  final offlineRequests = storageService.getData<List<Map<String, dynamic>>>(
    'offline_requests',
        (json) => List<Map<String, dynamic>>.from(json['requests'] as List),
      ) ?? [];
  
  if (offlineRequests.isNotEmpty) {
    for (final request in offlineRequests) {
      try {
        await dio.request(
          request['url'] as String,
          data: request['data'],
          options: Options(
            method: request['method'] as String,
            headers: Map<String, dynamic>.from(request['headers'] as Map),
          ),
        );
      } catch (e) {
        // Ignorer les erreurs pour éviter les boucles infinies
        print('Erreur lors du traitement de la requête offline: $e');
      }
    }
    
    // Vider la liste des requêtes offline
    await storageService.removeData('offline_requests');
  }
}
