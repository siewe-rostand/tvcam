abstract class AppException implements Exception {
  final String message;
  final String? code;
  final dynamic details;

  const AppException(this.message, {this.code, this.details});

  @override
  String toString() => 'AppException: $message';
}

class NetworkException extends AppException {
  const NetworkException(String message, {String? code, dynamic details})
      : super(message, code: code, details: details);

  @override
  String toString() => 'NetworkException: $message';
}

class OfflineException extends AppException {
  const OfflineException(String message, {String? code, dynamic details})
      : super(message, code: code, details: details);

  @override
  String toString() => 'OfflineException: $message';
}

class StorageException extends AppException {
  const StorageException(String message, {String? code, dynamic details})
      : super(message, code: code, details: details);

  @override
  String toString() => 'StorageException: $message';
}

class ValidationException extends AppException {
  const ValidationException(String message, {String? code, dynamic details})
      : super(message, code: code, details: details);

  @override
  String toString() => 'ValidationException: $message';
}

class AuthenticationException extends AppException {
  const AuthenticationException(String message, {String? code, dynamic details})
      : super(message, code: code, details: details);

  @override
  String toString() => 'AuthenticationException: $message';
}

class PermissionException extends AppException {
  const PermissionException(String message, {String? code, dynamic details})
      : super(message, code: code, details: details);

  @override
  String toString() => 'PermissionException: $message';
}

// Classe pour gérer les résultats des opérations
class Result<T> {
  final T? data;
  final AppException? error;
  final bool isSuccess;

  const Result._({
    this.data,
    this.error,
    required this.isSuccess,
  });

  factory Result.success(T data) => Result._(data: data, isSuccess: true);
  factory Result.failure(AppException error) => Result._(error: error, isSuccess: false);

  bool get isFailure => !isSuccess;

  T get value => data!;
  AppException get exception => error!;

  // Méthodes utilitaires
  Result<R> map<R>(R Function(T) transform) {
    if (isSuccess) {
      return Result.success(transform(data!));
    } else {
      return Result.failure(error!);
    }
  }

  Result<T> onSuccess(Function(T) callback) {
    if (isSuccess) {
      callback(data!);
    }
    return this;
  }

  Result<T> onFailure(Function(AppException) callback) {
    if (isFailure) {
      callback(error!);
    }
    return this;
  }
}
