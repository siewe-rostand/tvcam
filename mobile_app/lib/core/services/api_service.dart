import 'package:dio/dio.dart';
import 'package:logger/logger.dart';
import '../models/user.dart';
import '../models/customer.dart';
import '../models/bill.dart';
import '../models/issue.dart';
import '../models/notification.dart';

class ApiService {
  static const String baseUrl =
      'http://localhost:8080/api'; // À adapter selon votre configuration
  late final Dio _dio;
  final Logger _logger = Logger();

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
      return AuthResponse.fromJson(response.data['data']);
    } catch (e) {
      _logger.e('Login error: $e');
      rethrow;
    }
  }

  Future<AuthResponse> register(Map<String, dynamic> userData) async {
    try {
      final response = await _dio.post('/auth/register', data: userData);
      return AuthResponse.fromJson(response.data['data']);
    } catch (e) {
      _logger.e('Register error: $e');
      rethrow;
    }
  }

  // Méthodes pour les clients
  Future<List<Customer>> getCustomers({
    int page = 0,
    int size = 999999,
    String sortBy = 'createdAt',
    String direction = 'desc',
    String name = '',
  }) async {
    try {
      final response = await _dio.get('/customers', queryParameters: {
        'page': page,
        'size': size,
        'sortBy': sortBy,
        'direction': direction,
        'name': name,
      });

      final List<dynamic> data =
          response.data['content'] ?? response.data['data'] ?? [];
      return data.map((json) => Customer.fromJson(json)).toList();
    } catch (e) {
      _logger.e('Get customers error: $e');
      rethrow;
    }
  }

  Future<Customer> getCustomerById(int id) async {
    try {
      final response = await _dio.get('/customers/$id');
      return Customer.fromJson(response.data['data']);
    } catch (e) {
      _logger.e('Get customer by ID error: $e');
      rethrow;
    }
  }

  Future<Customer> createCustomer(Map<String, dynamic> customerData) async {
    try {
      final response = await _dio.post('/customers', data: customerData);
      return Customer.fromJson(response.data['data']);
    } catch (e) {
      _logger.e('Create customer error: $e');
      rethrow;
    }
  }

  Future<Customer> updateCustomer(
      int id, Map<String, dynamic> customerData) async {
    try {
      final response = await _dio.put('/customers/$id', data: customerData);
      return Customer.fromJson(response.data['data']);
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

  // Méthodes pour les factures
  Future<List<Bill>> getBills({
    int page = 0,
    int size = 999999,
    String sortBy = 'createdAt',
    String direction = 'desc',
  }) async {
    try {
      final response = await _dio.get('/bills', queryParameters: {
        'page': page,
        'size': size,
        'sortBy': sortBy,
        'direction': direction,
      });

      final List<dynamic> data =
          response.data['content'] ?? response.data['data'] ?? [];
      return data.map((json) => Bill.fromJson(json)).toList();
    } catch (e) {
      _logger.e('Get bills error: $e');
      rethrow;
    }
  }

  Future<List<Bill>> getBillsByCustomer(int customerId) async {
    try {
      final response = await _dio.get('/bills/customer/$customerId');
      final List<dynamic> data = response.data['data'] ?? [];
      return data.map((json) => Bill.fromJson(json)).toList();
    } catch (e) {
      _logger.e('Get bills by customer error: $e');
      rethrow;
    }
  }

  // Méthodes pour les paiements
  Future<List<Payment>> getPayments({
    int page = 0,
    int size = 999999,
    String sortBy = 'createdAt',
    String direction = 'desc',
    String name = '',
  }) async {
    try {
      final response = await _dio.get('/payments', queryParameters: {
        'page': page,
        'size': size,
        'sortBy': sortBy,
        'direction': direction,
        'name': name,
      });

      final List<dynamic> data =
          response.data['content'] ?? response.data['data'] ?? [];
      return data.map((json) => Payment.fromJson(json)).toList();
    } catch (e) {
      _logger.e('Get payments error: $e');
      rethrow;
    }
  }

  Future<List<Payment>> getPaymentsByCustomer(int customerId) async {
    try {
      final response = await _dio.get('/payments/$customerId');
      final List<dynamic> data = response.data['content'] ?? [];
      return data.map((json) => Payment.fromJson(json)).toList();
    } catch (e) {
      _logger.e('Get payments by customer error: $e');
      rethrow;
    }
  }

  Future<Payment> createPayment(Map<String, dynamic> paymentData) async {
    try {
      final response = await _dio.post('/payments', data: paymentData);
      return Payment.fromJson(response.data['data']);
    } catch (e) {
      _logger.e('Create payment error: $e');
      rethrow;
    }
  }

  // Méthodes pour les réclamations
  Future<List<Issue>> getIssues() async {
    try {
      final response = await _dio.get('/issues');
      final List<dynamic> data = response.data['data'] ?? [];
      return data.map((json) => Issue.fromJson(json)).toList();
    } catch (e) {
      _logger.e('Get issues error: $e');
      rethrow;
    }
  }

  Future<Issue> createIssue(IssueRequest issueRequest) async {
    try {
      final response = await _dio.post('/issues', data: issueRequest.toJson());
      return Issue.fromJson(response.data['data']);
    } catch (e) {
      _logger.e('Create issue error: $e');
      rethrow;
    }
  }

  // Méthodes pour les notifications
  Future<List<AppNotification>> getNotifications() async {
    try {
      final response = await _dio.get('/notifications');
      final List<dynamic> data = response.data['data'] ?? [];
      return data.map((json) => AppNotification.fromJson(json)).toList();
    } catch (e) {
      _logger.e('Get notifications error: $e');
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

  // Méthodes pour le dashboard
  Future<Map<String, dynamic>> getDashboardStats() async {
    try {
      final response = await _dio.get('/dashboard/stats');
      return response.data['data'] ?? {};
    } catch (e) {
      _logger.e('Get dashboard stats error: $e');
      rethrow;
    }
  }
}
