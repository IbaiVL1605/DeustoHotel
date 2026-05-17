@echo off

echo ===============================
echo Ejecutando tests de rendimiento
echo ===============================

jmeter -n ^
-t PerformanceTests\TestCompletos.jmx ^
-l PerformanceTests\resultados.jtl ^
-e -o PerformanceTests\reporte

echo ===============================
echo Test finalizado
echo ===============================

pause