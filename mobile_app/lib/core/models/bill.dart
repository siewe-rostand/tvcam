import 'package:tvcam_mobile/core/models/customer.dart'; // Assumed to be present

// --- BILL CLASS ---

class Bill {
  const Bill({
    this.billId,
    this.month,
    this.year,
    this.depositDate,
    this.deadline,
    this.debt,
    this.penalties,
    this.paidAmount,
    this.netToPay,
    this.monthlyPayment,
    this.observation,
    this.paymentStatus,
    this.currentPeriodBill = false,
    this.customer,
    this.payments,
    this.createdAt,
    this.updatedAt,
  });

  factory Bill.fromJson(Map<String, dynamic> json) {
    return Bill(
      billId: (json['billId'] as num?)?.toInt(),
      month: (json['month'] as num?)?.toInt(),
      year: (json['year'] as num?)?.toInt(),
      depositDate: json['depositDate'] != null
          ? DateTime.tryParse(json['depositDate'] as String)
          : null,
      deadline: json['deadline'] != null
          ? DateTime.tryParse(json['deadline'] as String)
          : null,
      debt: (json['debt'] as num?)?.toDouble(),
      penalties: (json['penalties'] as num?)?.toInt(),
      paidAmount: (json['paidAmount'] as num?)?.toDouble(),
      netToPay: (json['netToPay'] as num?)?.toDouble(),
      monthlyPayment: (json['monthlyPayment'] as num?)?.toDouble(),
      observation: json['observation'] as String?,
      paymentStatus: json['paymentStatus'] != null
          ? PaymentStatus.fromString(json['paymentStatus'] as String)
          : null,
      // Use as bool? ?? false for safety and default
      currentPeriodBill: json['currentPeriodBill'] as bool? ?? false,
      customer: json['customers'] != null
          ? Customer.fromJson(json['customers'] as Map<String, dynamic>)
          : null,
      payments: json['payments'] != null
          ? (json['payments'] as List)
              .map((payment) =>
                  Payment.fromJson(payment as Map<String, dynamic>))
              .toList()
          : null,
      createdAt: json['createdAt'] != null
          ? DateTime.tryParse(json['createdAt'] as String)
          : null,
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt'] as String)
          : null,
    );
  }

  final int? billId;
  final int? month;
  final int? year;
  final DateTime? depositDate; // Changed to DateTime?
  final DateTime? deadline; // Changed to DateTime?
  final double? debt;
  final int? penalties;
  final double? paidAmount;
  final double? netToPay;
  final double? monthlyPayment;
  final String? observation;
  final PaymentStatus? paymentStatus;
  final bool currentPeriodBill; // Changed to non-nullable with a default
  final Customer? customer;
  final List<Payment>? payments;
  final DateTime? createdAt; // Changed to DateTime?
  final DateTime? updatedAt; // Changed to DateTime?

  Bill copyWith({
    int? billId,
    int? month,
    int? year,
    DateTime? depositDate,
    DateTime? deadline,
    double? debt,
    int? penalties,
    double? paidAmount,
    double? netToPay,
    double? monthlyPayment,
    String? observation,
    PaymentStatus? paymentStatus,
    bool? currentPeriodBill,
    Customer? customer,
    List<Payment>? payments,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return Bill(
      billId: billId ?? this.billId,
      month: month ?? this.month,
      year: year ?? this.year,
      depositDate: depositDate ?? this.depositDate,
      deadline: deadline ?? this.deadline,
      debt: debt ?? this.debt,
      penalties: penalties ?? this.penalties,
      paidAmount: paidAmount ?? this.paidAmount,
      netToPay: netToPay ?? this.netToPay,
      monthlyPayment: monthlyPayment ?? this.monthlyPayment,
      observation: observation ?? this.observation,
      paymentStatus: paymentStatus ?? this.paymentStatus,
      currentPeriodBill: currentPeriodBill ?? this.currentPeriodBill,
      customer: customer ?? this.customer,
      payments: payments ?? this.payments,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'billId': billId,
      'month': month,
      'year': year,
      'depositDate': depositDate?.toIso8601String(),
      // Use ISO string for JSON
      'deadline': deadline?.toIso8601String(),
      // Use ISO string for JSON
      'debt': debt,
      'penalties': penalties,
      'paidAmount': paidAmount,
      'netToPay': netToPay,
      'monthlyPayment': monthlyPayment,
      'observation': observation,
      'paymentStatus': paymentStatus?.value,
      // Use .value for consistency with fromString
      'currentPeriodBill': currentPeriodBill,
      'customers': customer?.toJson(),
      'payments': payments?.map((payment) => payment.toJson()).toList(),
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  // --- Getters ---

  String get monthName {
    if (month == null || month! < 1 || month! > 12) return '';
    const months = [
      '',
      'Janvier',
      'Février',
      'Mars',
      'Avril',
      'Mai',
      'Juin',
      'Juillet',
      'Août',
      'Septembre',
      'Octobre',
      'Novembre',
      'Décembre'
    ];
    return months[month!];
  }

  String get periodDisplay => '$monthName ${year ?? ''}';

  bool get isPaid => paymentStatus == PaymentStatus.paid;

  bool get isOverdue {
    if (deadline == null) return false;
    // Check against the start of the current day to be more practical
    final today = DateTime.now().copyWith(
        hour: 0, minute: 0, second: 0, millisecond: 0, microsecond: 0);
    final deadlineDay = deadline!.copyWith(
        hour: 23, minute: 59, second: 59, millisecond: 999, microsecond: 999);

    // Bill is overdue if the deadline has passed (is before today) AND it is not paid.
    return deadlineDay.isBefore(today) && !isPaid;
  }

  // --- Standard Methods ---
  @override
  String toString() {
    return 'Bill(ID: $billId, Period: $periodDisplay, Status: ${paymentStatus?.displayName ?? 'N/A'})';
  }
}

// --- PAYMENT CLASS ---

class Payment {
  const Payment({
    this.paymentId,
    this.amount,
    this.paymentRef,
    this.observation,
    this.paymentMethod,
    this.paymentDate,
    this.updatedAt,
    this.createdAt,
    this.createdBy,
    this.modifiedBy,
    this.billId, // Keep billId to avoid recursion
    this.userId,
  });

  factory Payment.fromJson(Map<String, dynamic> json) {
    return Payment(
      paymentId: (json['paymentId'] as num?)?.toInt(),
      amount: (json['amount'] as num?)?.toDouble(),
      paymentRef: json['paymentRef'] as String?,
      observation: json['observation'] as String?,
      paymentMethod: json['paymentMethod'] != null
          ? PaymentMethod.fromString(json['paymentMethod'] as String)
          : null,
      paymentDate: json['paymentDate'] != null
          ? DateTime.tryParse(json['paymentDate'] as String)
          : null,
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt'] as String)
          : null,
      createdAt: json['createdAt'] != null
          ? DateTime.tryParse(json['createdAt'] as String)
          : null,
      createdBy: (json['createdBy'] as num?)?.toInt(),
      modifiedBy: (json['modifiedBy'] as num?)?.toInt(),
      billId: (json['billId'] as num?)?.toInt(),
      userId: (json['userId'] as num?)?.toInt(),
    );
  }

  final int? paymentId;
  final double? amount;
  final String? paymentRef;
  final String? observation;
  final PaymentMethod? paymentMethod;
  final DateTime? paymentDate; // Changed to DateTime?
  final DateTime? updatedAt; // Changed to DateTime?
  final DateTime? createdAt; // Changed to DateTime?
  final int? createdBy;
  final int? modifiedBy;
  final int? billId; // Simplified to ID to break circular dependency
  final int? userId;

  // --- Utility Methods ---

  /// Allows creating a new Payment instance with modified properties.
  Payment copyWith({
    int? paymentId,
    double? amount,
    String? paymentRef,
    String? observation,
    PaymentMethod? paymentMethod,
    DateTime? paymentDate,
    DateTime? updatedAt,
    DateTime? createdAt,
    int? createdBy,
    int? modifiedBy,
    int? billId,
    int? userId,
  }) {
    return Payment(
      paymentId: paymentId ?? this.paymentId,
      amount: amount ?? this.amount,
      paymentRef: paymentRef ?? this.paymentRef,
      observation: observation ?? this.observation,
      paymentMethod: paymentMethod ?? this.paymentMethod,
      paymentDate: paymentDate ?? this.paymentDate,
      updatedAt: updatedAt ?? this.updatedAt,
      createdAt: createdAt ?? this.createdAt,
      createdBy: createdBy ?? this.createdBy,
      modifiedBy: modifiedBy ?? this.modifiedBy,
      billId: billId ?? this.billId,
      userId: userId ?? this.userId,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'paymentId': paymentId,
      'amount': amount,
      'paymentRef': paymentRef,
      'observation': observation,
      'paymentMethod': paymentMethod?.value, // Use .value
      'paymentDate': paymentDate?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
      'createdAt': createdAt?.toIso8601String(),
      'createdBy': createdBy,
      'modifiedBy': modifiedBy,
      'billId': billId, // Corrected to ID
      'userId': userId,
    };
  }

  // --- Getters ---

  String get formattedDate {
    if (paymentDate == null) return '';

    // Example format: 14 oct. 2025
    return '${paymentDate!.day} ${_getMonthName(paymentDate!.month)} ${paymentDate!.year}';
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

  // --- Standard Methods ---
  @override
  String toString() {
    return 'Payment(ID: $paymentId, Amount: $amount, Date: $formattedDate)';
  }
}

// --- ENUMS (Corrected to use .value consistently) ---

enum PaymentStatus {
  pending('PENDING'),
  paid('PAID'),
  overdue('OVERDUE'),
  cancelled('CANCELLED');

  const PaymentStatus(this.value);

  final String value; // The raw JSON string value

  static PaymentStatus fromString(String value) {
    return PaymentStatus.values.firstWhere(
      (status) => status.value == value.toUpperCase(),
      orElse: () => PaymentStatus.pending,
    );
  }

  String get displayName {
    switch (this) {
      case PaymentStatus.pending:
        return 'En attente';
      case PaymentStatus.paid:
        return 'Payé';
      case PaymentStatus.overdue:
        return 'En retard';
      case PaymentStatus.cancelled:
        return 'Annulé';
    }
  }
}

enum PaymentMethod {
  cash('CASH'),
  mobileMoney('MOBILE_MONEY'),
  orangeMoney('ORANGE_MONEY'),
  bankTransfer('BANK_TRANSFER'),
  other('OTHER');

  const PaymentMethod(this.value);
  final String value;

  static PaymentMethod fromString(String value) {
    return PaymentMethod.values.firstWhere(
      (method) => method.value == value.toUpperCase(),
      orElse: () => PaymentMethod.cash,
    );
  }

  String get displayName {
    switch (this) {
      case PaymentMethod.cash:
        return 'Espèces';
      case PaymentMethod.mobileMoney:
        return 'MoMo';
      case PaymentMethod.orangeMoney:
        return 'Orange Money';
      case PaymentMethod.bankTransfer:
        return 'Virement bancaire';
      case PaymentMethod.other:
        return 'Autre';
    }
  }
}