class Customer {

  Customer({
    this.customerId,
    this.name,
    this.address,
    this.ref,
    this.telephone,
    this.hasDebt,
    this.hasPaid,
    this.isActive,
    this.isSuspended,
    this.paymentFrequency,
    this.zone,
    this.lastBillGenerationDate,
    this.createdAt,
    this.updatedAt,
  });

  factory Customer.fromJson(Map<String, dynamic> json) {
    return Customer(
      customerId: (json['customerId'] as num?)?.toInt(),
      name: json['name'] as String,
      address: json['address'] as String,
      ref: json['ref'] as String,
      telephone: json['telephone'] as String,
      hasDebt: json['hasDebt'] as bool,
      hasPaid: json['hasPaid'] as bool,
      isActive: json['isActive'] as bool,
      isSuspended: json['isSuspended'] as bool,
      paymentFrequency: json['paymentFrequency'] != null
          ? PaymentFrequency.fromString(json['paymentFrequency'] as String)
          : null,
      zone: json['zone'] != null
          ? Zone.fromJson(json['zone'] as Map<String, dynamic>)
          : null,
      lastBillGenerationDate: json['lastBillGenerationDate'] as String,
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String,
    );
  }
  final int? customerId;
  final String? name;
  final String? address;
  final String? ref;
  final String? telephone;
  final bool? hasDebt;
  final bool? hasPaid;
  final bool? isActive;
  final bool? isSuspended;
  final PaymentFrequency? paymentFrequency;
  final Zone? zone;
  final String? lastBillGenerationDate;
  final String? createdAt;
  final String? updatedAt;

  Map<String, dynamic> toJson() {
    return {
      'customerId': customerId,
      'name': name,
      'address': address,
      'ref': ref,
      'telephone': telephone,
      'hasDebt': hasDebt,
      'hasPaid': hasPaid,
      'isActive': isActive,
      'isSuspended': isSuspended,
      'paymentFrequency': paymentFrequency?.name,
      'zone': zone?.toJson(),
      'lastBillGenerationDate': lastBillGenerationDate,
      'createdAt': createdAt,
      'updatedAt': updatedAt,
    };
  }

  Customer copyWith({
    int? customerId,
    String? name,
    String? address,
    String? ref,
    String? telephone,
    bool? hasDebt,
    bool? hasPaid,
    bool? isActive,
    bool? isSuspended,
    PaymentFrequency? paymentFrequency,
    Zone? zone,
    String? lastBillGenerationDate,
    String? createdAt,
    String? updatedAt,
  }) {
    return Customer(
      customerId: customerId ?? this.customerId,
      name: name ?? this.name,
      address: address ?? this.address,
      ref: ref ?? this.ref,
      telephone: telephone ?? this.telephone,
      hasDebt: hasDebt ?? this.hasDebt,
      hasPaid: hasPaid ?? this.hasPaid,
      isActive: isActive ?? this.isActive,
      isSuspended: isSuspended ?? this.isSuspended,
      paymentFrequency: paymentFrequency ?? this.paymentFrequency,
      zone: zone ?? this.zone,
      lastBillGenerationDate:
          lastBillGenerationDate ?? this.lastBillGenerationDate,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}

class Zone {

  Zone({
    this.zoneId,
    this.name,
    this.description,
    this.isActive,
  });

  factory Zone.fromJson(Map<String, dynamic> json) {
    return Zone(
      zoneId: (json['zoneId'] as num?)?.toInt(),
      name: json['name'] as String,
      description: json['description'] as String,
      isActive: json['isActive'] as bool,
    );
  }
  final int? zoneId;
  final String? name;
  final String? description;
  final bool? isActive;

  Map<String, dynamic> toJson() {
    return {
      'zoneId': zoneId,
      'name': name,
      'description': description,
      'isActive': isActive,
    };
  }
}

enum PaymentFrequency {
  monthly('MONTHLY'),
  quarterly('QUARTERLY'),
  yearly('YEARLY');

  const PaymentFrequency(this.value);
  final String value;

  static PaymentFrequency fromString(String value) {
    switch (value.toUpperCase()) {
      case 'MONTHLY':
        return PaymentFrequency.monthly;
      case 'QUARTERLY':
        return PaymentFrequency.quarterly;
      case 'YEARLY':
        return PaymentFrequency.yearly;
      default:
        return PaymentFrequency.monthly;
    }
  }

  String get displayName {
    switch (this) {
      case PaymentFrequency.monthly:
        return 'Mensuel';
      case PaymentFrequency.quarterly:
        return 'Trimestriel';
      case PaymentFrequency.yearly:
        return 'Annuel';
    }
  }
}
