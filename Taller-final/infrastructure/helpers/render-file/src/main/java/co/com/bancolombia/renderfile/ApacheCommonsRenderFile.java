package co.com.bancolombia.renderfile;

import co.com.bancolombia.usecase.uploadmovements.RenderFileGateway;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Component
public class ApacheCommonsRenderFile implements RenderFileGateway {

    public Flux<Map<String, String>> render(byte[] bytes) {
        Reader in = new InputStreamReader(new ByteArrayInputStream(bytes));
        try {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(in);
            return Flux.fromIterable(records)
                    .map(CSVRecord::toMap);
        } catch (IOException e) {
            return Flux.error(new RuntimeException("Error parsing CSV data", e));
        }
    }

}
