import 'dart:convert';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'storage_service.g.dart';

@riverpod
class StorageService extends _$StorageService {
  late Box<String> _dataBox;
  late Box<String> _cacheBox;
  late Box<String> _settingsBox;
  
  @override
  Future<void> build() async {
    // Initialiser les boîtes Hive
    _dataBox = await Hive.openBox<String>('data_box');
    _cacheBox = await Hive.openBox<String>('cache_box');
    _settingsBox = await Hive.openBox<String>('settings_box');
  }
  
  // Stocker des données
  Future<void> storeData(String key, dynamic data) async {
    final jsonString = jsonEncode(data);
    await _dataBox.put(key, jsonString);
  }
  
  // Récupérer des données
  T? getData<T>(String key, T Function(Map<String, dynamic>) fromJson) {
    final jsonString = _dataBox.get(key);
    if (jsonString != null) {
      try {
        final jsonData = jsonDecode(jsonString) as Map<String, dynamic>;
        return fromJson(jsonData);
      } catch (e) {
        return null;
      }
    }
    return null;
  }
  
  // Stocker des données en cache
  Future<void> cacheData(String key, dynamic data, {Duration? expiry}) async {
    final cacheEntry = {
      'data': data,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
      'expiry': expiry?.inMilliseconds,
    };
    final jsonString = jsonEncode(cacheEntry);
    await _cacheBox.put(key, jsonString);
  }
  
  // Récupérer des données du cache
  T? getCachedData<T>(String key, T Function(Map<String, dynamic>) fromJson) {
    final jsonString = _cacheBox.get(key);
    if (jsonString != null) {
      try {
        final cacheEntry = jsonDecode(jsonString) as Map<String, dynamic>;
        final timestamp = cacheEntry['timestamp'] as int;
        final expiry = cacheEntry['expiry'] as int?;
        
        // Vérifier l'expiration
        if (expiry != null) {
          final now = DateTime.now().millisecondsSinceEpoch;
          if (now - timestamp > expiry) {
            _cacheBox.delete(key);
            return null;
          }
        }
        
        return fromJson(cacheEntry['data'] as Map<String, dynamic>);
      } catch (e) {
        return null;
      }
    }
    return null;
  }
  
  // Stocker des paramètres
  Future<void> setSetting(String key, String value) async {
    await _settingsBox.put(key, value);
  }
  
  // Récupérer des paramètres
  String? getSetting(String key) {
    return _settingsBox.get(key);
  }
  
  // Supprimer des données
  Future<void> removeData(String key) async {
    await _dataBox.delete(key);
  }
  
  // Supprimer du cache
  Future<void> clearCache() async {
    await _cacheBox.clear();
  }
  
  // Supprimer toutes les données
  Future<void> clearAll() async {
    await _dataBox.clear();
    await _cacheBox.clear();
    await _settingsBox.clear();
  }
  
  // Vérifier si des données existent
  bool hasData(String key) {
    return _dataBox.containsKey(key);
  }
  
  // Vérifier si des données en cache existent
  bool hasCachedData(String key) {
    return _cacheBox.containsKey(key);
  }
  
  // Obtenir la taille du cache
  int get cacheSize => _cacheBox.length;
  
  // Obtenir la taille des données
  int get dataSize => _dataBox.length;
}

// Provider pour le service de stockage
@riverpod
StorageService storageService(StorageServiceRef ref) {
  return ref.watch(storageServiceProvider.notifier);
}
