// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'connectivity_service.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

String _$isOfflineHash() => r'07db9e3517f67508e66b6b21253d280e9095d9a3';

/// See also [isOffline].
@ProviderFor(isOffline)
final isOfflineProvider = AutoDisposeProvider<bool>.internal(
  isOffline,
  name: r'isOfflineProvider',
  debugGetCreateSourceHash:
      const bool.fromEnvironment('dart.vm.product') ? null : _$isOfflineHash,
  dependencies: null,
  allTransitiveDependencies: null,
);

typedef IsOfflineRef = AutoDisposeProviderRef<bool>;
String _$connectivityStatusHash() =>
    r'85319d0a8486c17159bde9a1ef33d3b3e604b5c1';

/// See also [connectivityStatus].
@ProviderFor(connectivityStatus)
final connectivityStatusProvider =
    AutoDisposeProvider<AsyncValue<bool>>.internal(
  connectivityStatus,
  name: r'connectivityStatusProvider',
  debugGetCreateSourceHash: const bool.fromEnvironment('dart.vm.product')
      ? null
      : _$connectivityStatusHash,
  dependencies: null,
  allTransitiveDependencies: null,
);

typedef ConnectivityStatusRef = AutoDisposeProviderRef<AsyncValue<bool>>;
String _$connectivityServiceHash() =>
    r'61c57917d07962d2d710ba45f1b666894eac177d';

/// See also [ConnectivityService].
@ProviderFor(ConnectivityService)
final connectivityServiceProvider =
    AutoDisposeAsyncNotifierProvider<ConnectivityService, bool>.internal(
  ConnectivityService.new,
  name: r'connectivityServiceProvider',
  debugGetCreateSourceHash: const bool.fromEnvironment('dart.vm.product')
      ? null
      : _$connectivityServiceHash,
  dependencies: null,
  allTransitiveDependencies: null,
);

typedef _$ConnectivityService = AutoDisposeAsyncNotifier<bool>;
// ignore_for_file: type=lint
// ignore_for_file: subtype_of_sealed_class, invalid_use_of_internal_member, invalid_use_of_visible_for_testing_member
