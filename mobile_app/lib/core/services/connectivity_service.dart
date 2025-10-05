import 'dart:async';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'connectivity_service.g.dart';

@riverpod
class ConnectivityService extends _$ConnectivityService {
  late StreamSubscription<ConnectivityResult> _subscription;

  @override
  Future<bool> build() async {
    // Vérifier l'état initial de la connectivité
    final connectivityResult = await Connectivity().checkConnectivity();
    final isConnected = _isConnected(connectivityResult);

    // Écouter les changements de connectivité
    _subscription = Connectivity().onConnectivityChanged.listen((result) {
      final connected = _isConnected(result);
      state = AsyncValue.data(connected);
    });

    return isConnected;
  }

  bool _isConnected(ConnectivityResult result) {
    return result != ConnectivityResult.none;
  }

  void dispose() {
    _subscription.cancel();
  }
}

// Provider pour vérifier si l'app est en mode offline
@riverpod
bool isOffline(IsOfflineRef ref) {
  final connectivityAsync = ref.watch(connectivityServiceProvider);
  return connectivityAsync.when(
    data: (isConnected) => !isConnected,
    loading: () => false,
    error: (_, __) => true,
  );
}

// Provider pour obtenir l'état de la connectivité avec gestion d'erreur
@riverpod
AsyncValue<bool> connectivityStatus(ConnectivityStatusRef ref) {
  return ref.watch(connectivityServiceProvider);
}

// Extension pour faciliter l'utilisation
extension ConnectivityExtension on WidgetRef {
  bool get isOffline => read(isOfflineProvider);
  AsyncValue<bool> get connectivityStatus => watch(connectivityStatusProvider);
}
