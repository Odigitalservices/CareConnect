import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:careconnect_mobile/features/booking/data/booking_repository.dart';
import 'package:careconnect_mobile/features/booking/models/booking_model.dart';

abstract class BookingState extends Equatable {
  const BookingState();
  @override
  List<Object?> get props => [];
}

class BookingInitial extends BookingState {
  const BookingInitial();
}

class BookingLoading extends BookingState {
  const BookingLoading();
}

class BookingSuccess extends BookingState {
  final BookingModel booking;
  const BookingSuccess(this.booking);
  @override
  List<Object?> get props => [booking];
}

class BookingsLoaded extends BookingState {
  final List<BookingModel> bookings;
  const BookingsLoaded(this.bookings);
  @override
  List<Object?> get props => [bookings];
}

class BookingError extends BookingState {
  final String message;
  const BookingError(this.message);
  @override
  List<Object?> get props => [message];
}

class BookingCubit extends Cubit<BookingState> {
  final BookingRepository _repository;
  BookingCubit(this._repository) : super(const BookingInitial());

  Future<void> book(String slotId, String visitType) async {
    emit(const BookingLoading());
    try {
      final result = await _repository.create(slotId, visitType);
      emit(BookingSuccess(result));
    } catch (e) {
      emit(BookingError(e.toString()));
    }
  }

  Future<void> loadMyBookings() async {
    emit(const BookingLoading());
    try {
      final bookings = await _repository.getMyBookings();
      emit(BookingsLoaded(bookings));
    } catch (e) {
      emit(BookingError(e.toString()));
    }
  }
}
