class User {
  final int? userId;
  final String? username;
  final String? email;
  final String? firstName;
  final String? lastName;
  final String? phone;
  final String? address;
  final bool? isActive;
  final String? role;
  final String? createdAt;
  final String? updatedAt;

  User({
    this.userId,
    this.username,
    this.email,
    this.firstName,
    this.lastName,
    this.phone,
    this.address,
    this.isActive,
    this.role,
    this.createdAt,
    this.updatedAt,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      userId: json['userId'],
      username: json['username'],
      email: json['email'],
      firstName: json['firstName'],
      lastName: json['lastName'],
      phone: json['phone'],
      address: json['address'],
      isActive: json['isActive'],
      role: json['role'],
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'username': username,
      'email': email,
      'firstName': firstName,
      'lastName': lastName,
      'phone': phone,
      'address': address,
      'isActive': isActive,
      'role': role,
      'createdAt': createdAt,
      'updatedAt': updatedAt,
    };
  }

  String get fullName {
    if (firstName != null && lastName != null) {
      return '$firstName $lastName';
    }
    return firstName ?? lastName ?? username ?? '';
  }

  String get displayPhone {
    if (phone == null) return '';
    // Masquer une partie du numéro pour la sécurité
    if (phone!.length > 6) {
      return '${phone!.substring(0, 6)}XXX XX';
    }
    return phone!;
  }
}

class AuthRequest {
  final String username;
  final String password;

  AuthRequest({
    required this.username,
    required this.password,
  });

  Map<String, dynamic> toJson() {
    return {
      'username': username,
      'password': password,
    };
  }
}

class AuthResponse {
  final String? token;
  final String? refreshToken;
  final User? user;
  final String? message;
  final bool? success;

  AuthResponse({
    this.token,
    this.refreshToken,
    this.user,
    this.message,
    this.success,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'],
      refreshToken: json['refreshToken'],
      user: json['user'] != null ? User.fromJson(json['user']) : null,
      message: json['message'],
      success: json['success'],
    );
  }
}
