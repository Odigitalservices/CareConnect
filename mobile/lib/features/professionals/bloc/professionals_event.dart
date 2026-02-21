import 'package:equatable/equatable.dart';

abstract class ProfessionalsEvent extends Equatable {
  const ProfessionalsEvent();
  @override
  List<Object?> get props => [];
}

class ProfessionalsLoaded extends ProfessionalsEvent {
  const ProfessionalsLoaded();
}

class ProfessionalsFiltered extends ProfessionalsEvent {
  final String? city;
  final String? specialization;
  final double? minRate;
  final double? maxRate;

  const ProfessionalsFiltered({this.city, this.specialization, this.minRate, this.maxRate});

  @override
  List<Object?> get props => [city, specialization, minRate, maxRate];
}

class ProfessionalsNextPage extends ProfessionalsEvent {
  const ProfessionalsNextPage();
}
