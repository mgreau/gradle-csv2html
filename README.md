## Test

```bash
mkdir -p /tmp/csv2html/input /tmp/csv2html/output
curl -o /tmp/csv2html/input/elasticsearch-7.7.1.csv  https://artifacts.elastic.co/reports/dependencies/elasticsearch-7.7.1.csv
curl -o /tmp/csv2html/input/ml-cpp-7.7.1.csv  https://artifacts.elastic.co/reports/dependencies/ml-cpp-7.7.1.csv 
./gradlew csv2html
open /tmp/csv2html/output/report.html
```
