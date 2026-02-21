// ignore_for_file: constant_identifier_names

enum BookingStatus { PENDING, CONFIRMED, COMPLETED, CANCELLED }
enum VisitType { HOME, CLINIC }

class BookingModel {
  final String id;
  final String professionalFirstName;
  final String professionalLastName;
  final String specialization;
  final DateTime slotStart;
  final DateTime slotEnd;
  final BookingStatus status;
  final VisitType visitType;

  const BookingModel({
    required this.id,
    required this.professionalFirstName,
    required this.professionalLastName,
    required this.specialization,
    required this.slotStart,
    required this.slotEnd,
    required this.status,
    required this.visitType,
  });

  String get professionalName => '$professionalFirstName $professionalLastName';

  factory BookingModel.fromJson(Map<String, dynamic> json) {
    return BookingModel(
      id: json['id'] as String,
      professionalFirstName: json['professionalFirstName'] as String,
      professionalLastName: json['professionalLastName'] as String,
      specialization: json['specialization'] as String,
      slotStart: DateTime.parse(json['slotStart'] as String),
      slotEnd: DateTime.parse(json['slotEnd'] as String),
      status: BookingStatus.values.byName(json['status'] as String),
      visitType: VisitType.values.byName(json['visitType'] as String),
    );
  }
}
