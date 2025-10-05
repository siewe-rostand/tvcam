class Issue {
  final int? issueId;
  final String? subject;
  final String? message;
  final String? status;
  final String? priority;
  final int? customerId;
  final int? userId;
  final String? createdAt;
  final String? updatedAt;
  final String? resolvedAt;

  Issue({
    this.issueId,
    this.subject,
    this.message,
    this.status,
    this.priority,
    this.customerId,
    this.userId,
    this.createdAt,
    this.updatedAt,
    this.resolvedAt,
  });

  factory Issue.fromJson(Map<String, dynamic> json) {
    return Issue(
      issueId: json['issueId'],
      subject: json['subject'],
      message: json['message'],
      status: json['status'],
      priority: json['priority'],
      customerId: json['customerId'],
      userId: json['userId'],
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
      resolvedAt: json['resolvedAt'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'issueId': issueId,
      'subject': subject,
      'message': message,
      'status': status,
      'priority': priority,
      'customerId': customerId,
      'userId': userId,
      'createdAt': createdAt,
      'updatedAt': updatedAt,
      'resolvedAt': resolvedAt,
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

  bool get isResolved {
    return status?.toLowerCase() == 'resolved' || resolvedAt != null;
  }

  IssuePriority get issuePriority {
    switch (priority?.toLowerCase()) {
      case 'high':
        return IssuePriority.high;
      case 'medium':
        return IssuePriority.medium;
      case 'low':
        return IssuePriority.low;
      default:
        return IssuePriority.medium;
    }
  }
}

enum IssuePriority {
  low('LOW'),
  medium('MEDIUM'),
  high('HIGH');

  const IssuePriority(this.value);
  final String value;

  String get displayName {
    switch (this) {
      case IssuePriority.low:
        return 'Faible';
      case IssuePriority.medium:
        return 'Moyenne';
      case IssuePriority.high:
        return 'Élevée';
    }
  }
}

class IssueRequest {
  final String subject;
  final String message;
  final int? customerId;

  IssueRequest({
    required this.subject,
    required this.message,
    this.customerId,
  });

  Map<String, dynamic> toJson() {
    return {
      'subject': subject,
      'message': message,
      'customerId': customerId,
    };
  }
}
