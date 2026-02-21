import 'package:careconnect_mobile/features/booking/models/booking_model.dart';

abstract class BookingRepository {
  Future<BookingModel> create(String slotId, String visitType);
  Future<List<BookingModel>> getMyBookings();
  Future<BookingModel> cancel(String bookingId);
}
