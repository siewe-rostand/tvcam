class User {

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
      userId: (json['userId'] as num?)?.toInt(),
      username: json['username'] as String,
      email: json['email'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      phone: json['phone'] as String,
      address: json['address'] as String,
      isActive: json['isActive'] as bool,
      role: json['role'] as String,
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String,
    );
  }
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
    if (phone!.length > 6) {
      return '${phone!.substring(0, 6)}XXX XX';
    }
    return phone!;
  }
}

class AuthRequest {
  AuthRequest({
    required this.username,
    required this.password,
  });
  final String username;
  final String password;

  Map<String, dynamic> toJson() {
    return {
      'username': username,
      'password': password,
    };
  }
}

class AuthResponse {
  AuthResponse({
    this.token,
    this.refreshToken,
    this.user,
    this.message,
    this.success,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'] as String,
      refreshToken: json['refreshToken'] as String,
      user: json['user'] != null
          ? User.fromJson(json['user'] as Map<String, dynamic>)
          : null,
      message: json['message'] as String,
      success: json['success'] as bool,
    );
  }
  final String? token;
  final String? refreshToken;
  final User? user;
  final String? message;
  final bool? success;
}
