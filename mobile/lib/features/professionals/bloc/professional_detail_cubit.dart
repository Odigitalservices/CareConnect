import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:careconnect_mobile/features/professionals/data/professional_repository.dart';
import 'package:careconnect_mobile/features/professionals/models/professional_detail.dart';

// States
abstract class ProfessionalDetailState extends Equatable {
  const ProfessionalDetailState();
  @override
  List<Object?> get props => [];
}

class ProfessionalDetailInitial extends ProfessionalDetailState {
  const ProfessionalDetailInitial();
}

class ProfessionalDetailLoading extends ProfessionalDetailState {
  const ProfessionalDetailLoading();
}

class ProfessionalDetailLoaded extends ProfessionalDetailState {
  final ProfessionalDetail professional;
  const ProfessionalDetailLoaded(this.professional);
  @override
  List<Object?> get props => [professional];
}

class ProfessionalDetailError extends ProfessionalDetailState {
  final String message;
  const ProfessionalDetailError(this.message);
  @override
  List<Object?> get props => [message];
}

// Cubit
class ProfessionalDetailCubit extends Cubit<ProfessionalDetailState> {
  final ProfessionalRepository _repository;

  ProfessionalDetailCubit(this._repository) : super(const ProfessionalDetailInitial());

  Future<void> load(String id) async {
    emit(const ProfessionalDetailLoading());
    try {
      final professional = await _repository.getById(id);
      emit(ProfessionalDetailLoaded(professional));
    } catch (e) {
      emit(ProfessionalDetailError(e.toString()));
    }
  }
}
