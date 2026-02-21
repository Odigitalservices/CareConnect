import 'package:equatable/equatable.dart';
import 'package:careconnect_mobile/features/professionals/models/professional_summary.dart';

abstract class ProfessionalsState extends Equatable {
  const ProfessionalsState();
  @override
  List<Object?> get props => [];
}

class ProfessionalsInitial extends ProfessionalsState {
  const ProfessionalsInitial();
}

class ProfessionalsLoading extends ProfessionalsState {
  const ProfessionalsLoading();
}

class ProfessionalsLoaded extends ProfessionalsState {
  final List<ProfessionalSummary> professionals;
  final bool hasMore;
  final int currentPage;
  final String? city;
  final String? specialization;

  const ProfessionalsLoaded({
    required this.professionals,
    required this.hasMore,
    required this.currentPage,
    this.city,
    this.specialization,
  });

  @override
  List<Object?> get props => [professionals, hasMore, currentPage, city, specialization];
}

class ProfessionalsError extends ProfessionalsState {
  final String message;
  const ProfessionalsError(this.message);
  @override
  List<Object?> get props => [message];
}
