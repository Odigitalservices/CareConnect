import 'package:flutter_test/flutter_test.dart';
import 'package:careconnect_mobile/main.dart';

void main() {
  testWidgets('CareConnect app smoke test', (WidgetTester tester) async {
    await tester.pumpWidget(const CareConnectApp());
    expect(find.text('CareConnect'), findsWidgets);
  });
}
