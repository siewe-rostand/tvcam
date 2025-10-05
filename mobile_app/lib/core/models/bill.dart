import 'customer.dart';

class Bill {
  final int? billId;
  final int? month;
  final int? year;
  final String? depositDate;
  final String? deadline;
  final double? debt;
  final int? penalties;
  final double? paidAmount;
  final double? netToPay;
  final double? monthlyPayment;
  final String? observation;
  final PaymentStatus? paymentStatus;
  final bool? currentPeriodBill;
  final Customer? customers;
  final List<Payment>? payments;
  final String? createdAt;
  final String? updatedAt;

  Bill({
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
    this.currentPeriodBill,
    this.customers,
    this.payments,
    this.createdAt,
    this.updatedAt,
  });

  factory Bill.fromJson(Map<String, dynamic> json) {
    return Bill(
      billId: json['billId'],
      month: json['month'],
      year: json['year'],
      depositDate: json['depositDate'],
      deadline: json['deadline'],
      debt: json['debt']?.toDouble(),
      penalties: json['penalties'],
      paidAmount: json['paidAmount']?.toDouble(),
      netToPay: json['netToPay']?.toDouble(),
      monthlyPayment: json['monthlyPayment']?.toDouble(),
      observation: json['observation'],
      paymentStatus: json['paymentStatus'] != null
          ? PaymentStatus.fromString(json['paymentStatus'])
          : null,
      currentPeriodBill: json['currentPeriodBill'],
      customers: json['customers'] != null
          ? Customer.fromJson(json['customers'])
          : null,
      payments: json['payments'] != null
          ? (json['payments'] as List)
              .map((payment) => Payment.fromJson(payment))
              .toList()
          : null,
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'billId': billId,
      'month': month,
      'year': year,
      'depositDate': depositDate,
      'deadline': deadline,
      'debt': debt,
      'penalties': penalties,
      'paidAmount': paidAmount,
      'netToPay': netToPay,
      'monthlyPayment': monthlyPayment,
      'observation': observation,
      'paymentStatus': paymentStatus?.name,
      'currentPeriodBill': currentPeriodBill,
      'customers': customers?.toJson(),
      'payments': payments?.map((payment) => payment.toJson()).toList(),
      'createdAt': createdAt,
      'updatedAt': updatedAt,
    };
  }

  String get monthName {
    if (month == null) return '';
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

  String get periodDisplay {
    return '${monthName} ${year ?? ''}';
  }

  bool get isPaid {
    return paymentStatus == PaymentStatus.paid;
  }

  bool get isOverdue {
    if (deadline == null) return false;
    final deadlineDate = DateTime.tryParse(deadline!);
    if (deadlineDate == null) return false;
    return DateTime.now().isAfter(deadlineDate) && !isPaid;
  }
}

class Payment {
  final int? paymentId;
  final double? amount;
  final String? paymentRef;
  final String? observation;
  final PaymentMethod? paymentMethod;
  final String? paymentDate;
  final String? updatedAt;
  final String? createdAt;
  final int? createdBy;
  final int? modifiedBy;
  final Bill? bills;
  final int? userId;

  Payment({
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
    this.bills,
    this.userId,
  });

  factory Payment.fromJson(Map<String, dynamic> json) {
    return Payment(
      paymentId: json['paymentId'],
      amount: json['amount']?.toDouble(),
      paymentRef: json['paymentRef'],
      observation: json['observation'],
      paymentMethod: json['paymentMethod'] != null
          ? PaymentMethod.fromString(json['paymentMethod'])
          : null,
      paymentDate: json['paymentDate'],
      updatedAt: json['updatedAt'],
      createdAt: json['createdAt'],
      createdBy: json['createdBy'],
      modifiedBy: json['modifiedBy'],
      bills: json['bills'] != null ? Bill.fromJson(json['bills']) : null,
      userId: json['userId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'paymentId': paymentId,
      'amount': amount,
      'paymentRef': paymentRef,
      'observation': observation,
      'paymentMethod': paymentMethod?.name,
      'paymentDate': paymentDate,
      'updatedAt': updatedAt,
      'createdAt': createdAt,
      'createdBy': createdBy,
      'modifiedBy': modifiedBy,
      'bills': bills?.toJson(),
      'userId': userId,
    };
  }

  String get formattedDate {
    if (paymentDate == null) return '';
    try {
      final date = DateTime.parse(paymentDate!);
      return '${date.day} ${_getMonthName(date.month)} ${date.year}';
    } catch (e) {
      return paymentDate!;
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
}

enum PaymentStatus {
  pending('PENDING'),
  paid('PAID'),
  overdue('OVERDUE'),
  cancelled('CANCELLED');

  const PaymentStatus(this.value);
  final String value;

  static PaymentStatus fromString(String value) {
    switch (value.toUpperCase()) {
      case 'PENDING':
        return PaymentStatus.pending;
      case 'PAID':
        return PaymentStatus.paid;
      case 'OVERDUE':
        return PaymentStatus.overdue;
      case 'CANCELLED':
        return PaymentStatus.cancelled;
      default:
        return PaymentStatus.pending;
    }
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
    switch (value.toUpperCase()) {
      case 'CASH':
        return PaymentMethod.cash;
      case 'MOBILE_MONEY':
        return PaymentMethod.mobileMoney;
      case 'ORANGE_MONEY':
        return PaymentMethod.orangeMoney;
      case 'BANK_TRANSFER':
        return PaymentMethod.bankTransfer;
      case 'OTHER':
        return PaymentMethod.other;
      default:
        return PaymentMethod.cash;
    }
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
