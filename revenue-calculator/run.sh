echo "=== Compiling ==="
mkdir -p out
javac -d out src/main/java/org/example/enums/CustomerType.java src/main/java/org/example/model/Order.java src/main/java/org/example/revenuecalculator/MonthlyRevenueCalculator.java src/main/java/org/example/revenuecalculator/MonthlyRevenueCalculatorMain.java
echo "=== Running ==="
java -cp out org.example.revenuecalculator.MonthlyRevenueCalculatorMain