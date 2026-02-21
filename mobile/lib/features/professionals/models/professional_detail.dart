import 'package:careconnect_mobile/features/professionals/models/professional_summary.dart';

class AvailabilitySlot {
  final String id;
  final DateTime startTime;
  final DateTime endTime;

  const AvailabilitySlot({
    required this.id,
    required this.startTime,
    required this.endTime,
  });

  factory AvailabilitySlot.fromJson(Map<String, dynamic> json) {
    return AvailabilitySlot(
      id: json['id'] as String,
      startTime: DateTime.parse(json['startTime'] as String),
      endTime: DateTime.parse(json['endTime'] as String),
    );
  }
}

class ProfessionalDetail extends ProfessionalSummary {
  final String? bio;
  final List<AvailabilitySlot> availableSlots;

  const ProfessionalDetail({
    required super.id,
    required super.firstName,
    required super.lastName,
    required super.specialization,
    super.city,
    super.hourlyRate,
    this.bio,
    required this.availableSlots,
  });

  factory ProfessionalDetail.fromJson(Map<String, dynamic> json) {
    return ProfessionalDetail(
      id: json['id'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      specialization: json['specialization'] as String,
      city: json['city'] as String?,
      hourlyRate: (json['hourlyRate'] as num?)?.toDouble(),
      bio: json['bio'] as String?,
      availableSlots: (json['availableSlots'] as List<dynamic>)
          .map((e) => AvailabilitySlot.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }
}
