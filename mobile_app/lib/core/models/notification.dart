class AppNotification {
  AppNotification({
    this.notificationId,
    this.title,
    this.message,
    this.type,
    this.isRead,
    this.data,
    this.userId,
    this.createdAt,
    this.updatedAt,
  });

  factory AppNotification.fromJson(Map<String, dynamic> json) {
    return AppNotification(
      notificationId: (json['notificationId'] as num?)?.toInt(),
      title: json['title'] as String,
      message: json['message'] as String,
      type: json['type'] as String,
      isRead: json['isRead'] as bool,
      data: json['data'] as String,
      userId: (json['userId'] as num?)?.toInt(),
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String,
    );
  }
  final int? notificationId;
  final String? title;
  final String? message;
  final String? type;
  final bool? isRead;
  final String? data;
  final int? userId;
  final String? createdAt;
  final String? updatedAt;

  Map<String, dynamic> toJson() {
    return {
      'notificationId': notificationId,
      'title': title,
      'message': message,
      'type': type,
      'isRead': isRead,
      'data': data,
      'userId': userId,
      'createdAt': createdAt,
      'updatedAt': updatedAt,
    };
  }

  String get formattedDate {
    if (createdAt == null) return '';
    try {
      final date = DateTime.parse(createdAt!);
      return '${date.day} ${_getMonthName(date.month)} ${date.year}';
    } catch (e) {
      return createdAt!;
    }
  }

  String _getMonthName(int month) {
    const months = [
      '',
      'janv.',
      'févr.',
      'mars',
      'avr.',
      'mai',
      'juin',
      'juil.',
      'août',
      'sept.',
      'oct.',
      'nov.',
      'déc.'
    ];
    return months[month];
  }

  NotificationType get notificationType {
    switch (type?.toLowerCase()) {
      case 'payment':
        return NotificationType.payment;
      case 'bill':
        return NotificationType.bill;
      case 'system':
        return NotificationType.system;
      case 'warning':
        return NotificationType.warning;
      default:
        return NotificationType.system;
    }
  }
}

enum NotificationType {
  payment('PAYMENT'),
  bill('BILL'),
  system('SYSTEM'),
  warning('WARNING');

  const NotificationType(this.value);
  final String value;

  String get displayName {
    switch (this) {
      case NotificationType.payment:
        return 'Paiement';
      case NotificationType.bill:
        return 'Facture';
      case NotificationType.system:
        return 'Système';
      case NotificationType.warning:
        return 'Avertissement';
    }
  }

  String get iconName {
    switch (this) {
      case NotificationType.payment:
        return 'check_circle';
      case NotificationType.bill:
        return 'receipt';
      case NotificationType.system:
        return 'info';
      case NotificationType.warning:
        return 'warning';
    }
  }
}
