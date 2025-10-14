import 'dart:convert';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:logger/logger.dart';
import '../models/user.dart';
import 'api_service.dart';

part 'auth_service.g.dart';

@riverpod
class AuthService extends _$AuthService {
  final Logger _logger = Logger();
  late final SharedPreferences _prefs;

  @override
  Future<AuthState> build() async {
    _prefs = await SharedPreferences.getInstance();
    final token = _prefs.getString('auth_token');
    final userJson = _prefs.getString('user_data');

    if (token != null && userJson != null) {
      try {
        final user = User.fromJson(jsonDecode(userJson));
        return AuthState.authenticated(user, token);
      } catch (e) {
        _logger.e('Error parsing user data: $e');
        await _clearAuthData();
      }
    }

    return const AuthState.unauthenticated();
  }

  Future<AuthResponse> login(String username, String password) async {
    try {
      state = const AsyncValue.loading();

      final apiService = ref.read(apiServiceProvider);
      final authRequest = AuthRequest(username: username, password: password);
      final response = await apiService.login(authRequest);

      if (response.success == true &&
          response.token != null &&
          response.user != null) {
        await _saveAuthData(response.token!, response.user!);

        // Mettre à jour l'état
        state = AsyncValue.data(
            AuthState.authenticated(response.user!, response.token!));

        return response;
      } else {
        throw Exception(response.message ?? 'Échec de la connexion');
      }
    } catch (e) {
      _logger.e('Login error: $e');
      state = AsyncValue.error(e, StackTrace.current);
      rethrow;
    }
  }

  Future<AuthResponse> register(Map<String, dynamic> userData) async {
    try {
      state = const AsyncValue.loading();

      final apiService = ref.read(apiServiceProvider);
      final response = await apiService.register(userData);

      if (response.success == true &&
          response.token != null &&
          response.user != null) {
        // Sauvegarder les données d'authentification
        await _saveAuthData(response.token!, response.user!);

        // Mettre à jour l'état
        state = AsyncValue.data(
            AuthState.authenticated(response.user!, response.token!));

        return response;
      } else {
        throw Exception(response.message ?? 'Échec de l\'inscription');
      }
    } catch (e) {
      _logger.e('Register error: $e');
      state = AsyncValue.error(e, StackTrace.current);
      rethrow;
    }
  }

  Future<void> logout() async {
    try {
      await _clearAuthData();
      state = const AsyncValue.data(AuthState.unauthenticated());
    } catch (e) {
      _logger.e('Logout error: $e');
    }
  }

  Future<void> _saveAuthData(String token, User user) async {
    await _prefs.setString('auth_token', token);
    await _prefs.setString('user_data', jsonEncode(user.toJson()));

    // Mettre à jour le token dans l'API service
    final apiService = ref.read(apiServiceProvider);
    apiService.setAuthToken(token);
  }

  Future<void> _clearAuthData() async {
    await _prefs.remove('auth_token');
    await _prefs.remove('user_data');

    // Nettoyer le token dans l'API service
    final apiService = ref.read(apiServiceProvider);
    apiService.clearAuthToken();
  }

  bool get isAuthenticated {
    return state.valueOrNull?.isAuthenticated ?? false;
  }

  User? get currentUser {
    return state.valueOrNull?.user;
  }

  String? get token {
    return state.valueOrNull?.token;
  }
}

@riverpod
ApiService apiService(ApiServiceRef ref) {
  return ApiService();
}

// État d'authentification
sealed class AuthState {
  const AuthState();

  const factory AuthState.unauthenticated() = Unauthenticated;
  const factory AuthState.authenticated(User user, String token) =
      Authenticated;

  bool get isAuthenticated => this is Authenticated;
  User? get user => switch (this) {
        Authenticated(user: final user) => user,
        _ => null,
      };
  String? get token => switch (this) {
        Authenticated(token: final token) => token,
        _ => null,
      };
}

class Unauthenticated extends AuthState {
  const Unauthenticated();
}

class Authenticated extends AuthState {
  const Authenticated(this.user, this.token);
  final User user;
  final String token;
}
