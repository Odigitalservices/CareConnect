import 'package:equatable/equatable.dart';

class ProfessionalSummary extends Equatable {
  final String id;
  final String firstName;
  final String lastName;
  final String specialization;
  final String? city;
  final double? hourlyRate;

  const ProfessionalSummary({
    required this.id,
    required this.firstName,
    required this.lastName,
    required this.specialization,
    this.city,
    this.hourlyRate,
  });

  factory ProfessionalSummary.fromJson(Map<String, dynamic> json) {
    return ProfessionalSummary(
      id: json['id'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      specialization: json['specialization'] as String,
      city: json['city'] as String?,
      hourlyRate: (json['hourlyRate'] as num?)?.toDouble(),
    );
  }

  String get fullName => '$firstName $lastName';

  @override
  List<Object?> get props => [id, firstName, lastName, specialization, city, hourlyRate];
}
