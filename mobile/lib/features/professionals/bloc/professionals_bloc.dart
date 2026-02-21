import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:careconnect_mobile/features/professionals/bloc/professionals_event.dart' as evt;
import 'package:careconnect_mobile/features/professionals/bloc/professionals_state.dart';
import 'package:careconnect_mobile/features/professionals/data/professional_repository.dart';
import 'package:careconnect_mobile/features/professionals/models/professional_summary.dart';

class ProfessionalsBloc extends Bloc<evt.ProfessionalsEvent, ProfessionalsState> {
  final ProfessionalRepository _repository;

  ProfessionalsBloc(this._repository) : super(const ProfessionalsInitial()) {
    on<evt.ProfessionalsLoaded>(_onLoaded);
    on<evt.ProfessionalsFiltered>(_onFiltered);
    on<evt.ProfessionalsNextPage>(_onNextPage);
  }

  Future<void> _onLoaded(evt.ProfessionalsLoaded event, Emitter<ProfessionalsState> emit) async {
    emit(const ProfessionalsLoading());
    try {
      final result = await _repository.list(page: 0);
      emit(ProfessionalsLoaded(
        professionals: result.content,
        hasMore: !result.last,
        currentPage: 0,
      ));
    } catch (e) {
      emit(ProfessionalsError(e.toString()));
    }
  }

  Future<void> _onFiltered(evt.ProfessionalsFiltered event, Emitter<ProfessionalsState> emit) async {
    emit(const ProfessionalsLoading());
    try {
      final result = await _repository.list(
        city: event.city,
        specialization: event.specialization,
        minRate: event.minRate,
        maxRate: event.maxRate,
        page: 0,
      );
      emit(ProfessionalsLoaded(
        professionals: result.content,
        hasMore: !result.last,
        currentPage: 0,
        city: event.city,
        specialization: event.specialization,
      ));
    } catch (e) {
      emit(ProfessionalsError(e.toString()));
    }
  }

  Future<void> _onNextPage(evt.ProfessionalsNextPage event, Emitter<ProfessionalsState> emit) async {
    final current = state;
    if (current is! ProfessionalsLoaded || !current.hasMore) return;
    try {
      final nextPage = current.currentPage + 1;
      final result = await _repository.list(
        city: current.city,
        specialization: current.specialization,
        page: nextPage,
      );
      final merged = List<ProfessionalSummary>.from(current.professionals)
        ..addAll(result.content);
      emit(ProfessionalsLoaded(
        professionals: merged,
        hasMore: !result.last,
        currentPage: nextPage,
        city: current.city,
        specialization: current.specialization,
      ));
    } catch (e) {
      emit(ProfessionalsError(e.toString()));
    }
  }
}
