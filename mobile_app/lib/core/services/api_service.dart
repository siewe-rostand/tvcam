import 'package:dio/dio.dart';
import 'package:logger/logger.dart';

import '../models/bill.dart';
import '../models/customer.dart';
import '../models/issue.dart';
import '../models/notification.dart';
import '../models/user.dart';

class ApiService {
  ApiService() {
    _dio = Dio(BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
      headers: {
        'Content-Type': 'application/json',
      },
    ));

    // Intercepteur pour les logs
    _dio.interceptors.add(LogInterceptor(
      requestBody: true,
      responseBody: true,
      logPrint: (obj) => _logger.d(obj),
    ));

    // Intercepteur pour l'authentification
    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) {
        // Ajouter le token d'authentification si disponible
        final token = _getAuthToken();
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        handler.next(options);
      },
      onError: (error, handler) {
        _logger.e('API Error: ${error.message}');
        handler.next(error);
      },
    ));
  }
  static const String baseUrl = 'http://localhost:8080/api';
  late final Dio _dio;
  final Logger _logger = Logger();

  String? _getAuthToken() {
    // TODO: Récupérer le token depuis le stockage local
    return null;
  }

  void setAuthToken(String token) {
    _dio.options.headers['Authorization'] = 'Bearer $token';
  }

  void clearAuthToken() {
    _dio.options.headers.remove('Authorization');
  }

  // Méthodes d'authentification
  Future<AuthResponse> login(AuthRequest request) async {
    try {
      final response = await _dio.post('/auth/login', data: request.toJson());
      final res = response.data;
      return AuthResponse.fromJson(res as Map<String, dynamic>);
    } catch (e) {
      _logger.e('Login error: $e');
      rethrow;
    }
  }

  Future<AuthResponse> register(Map<String, dynamic> userData) async {
    try {
      final response = await _dio.post('/auth/register', data: userData);
      final res = response.data as Map<String, dynamic>;
      return AuthResponse.fromJson(res);
    } catch (e) {
      _logger.e('Register error: $e');
      rethrow;
    }
  }

  // Méthodes pour les clients
  // Future<List<Customer>> getCustomers({
  //   int page = 0,
  //   int size = 999999,
  //   String sortBy = 'createdAt',
  //   String direction = 'desc',
  //   String name = '',
  // }) async {
  //   try {
  //     final response = await _dio.get('/customers', queryParameters: {
  //       'page': page,
  //       'size': size,
  //       'sortBy': sortBy,
  //       'direction': direction,
  //       'name': name,
  //     });
  //
  //     final List<dynamic> data =
  //         response.data['content'] ?? response.data['data'] ?? [];
  //     return data.map((json) => Customer.fromJson(json)).toList();
  //   } catch (e) {
  //     _logger.e('Get customers error: $e');
  //     rethrow;
  //   }
  // }

  Future<Customer> getCustomerById(int id) async {
    try {
      final response = await _dio.get('/customers/$id');
      final res = response.data as Map<String, dynamic>;
      return Customer.fromJson(res);
    } catch (e) {
      _logger.e('Get customer by ID error: $e');
      rethrow;
    }
  }

  Future<Customer> createCustomer(Map<String, dynamic> customerData) async {
    try {
      final response = await _dio.post('/customers', data: customerData);
      final res = response.data as Map<String, dynamic>;
      return Customer.fromJson(res);
    } catch (e) {
      _logger.e('Create customer error: $e');
      rethrow;
    }
  }

  Future<Customer> updateCustomer(
      int id, Map<String, dynamic> customerData) async {
    try {
      final response = await _dio.put('/customers/$id', data: customerData);
      final res = response.data as Map<String, dynamic>;
      return Customer.fromJson(res);
    } catch (e) {
      _logger.e('Update customer error: $e');
      rethrow;
    }
  }

  Future<void> deleteCustomer(int id) async {
    try {
      await _dio.delete('/customers/$id');
    } catch (e) {
      _logger.e('Delete customer error: $e');
      rethrow;
    }
  }


  Future<void> markNotificationAsRead(int notificationId) async {
    try {
      await _dio.put('/notifications/$notificationId/read');
    } catch (e) {
      _logger.e('Mark notification as read error: $e');
      rethrow;
    }
  }

}
