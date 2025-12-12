package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.entity.Ficha;
import com.sena.sistemaintegralsena.repository.AprendizRepository;
import com.sena.sistemaintegralsena.repository.FichaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class AprendizExcelService {

    private final AprendizRepository aprendizRepository;
    private final FichaRepository fichaRepository;

    public AprendizExcelService(AprendizRepository aprendizRepository, FichaRepository fichaRepository) {
        this.aprendizRepository = aprendizRepository;
        this.fichaRepository = fichaRepository;
    }

    public void guardar(MultipartFile file) {
        try {
            List<Aprendiz> aprendices = excelToAprendices(file.getInputStream());
            aprendizRepository.saveAll(aprendices);
        } catch (IOException e) {
            throw new RuntimeException("Fallo al analizar el archivo Excel: " + e.getMessage());
        }
    }

    private List<Aprendiz> excelToAprendices(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<Aprendiz> aprendices = new ArrayList<>();

            int rowNumber = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                // Verificar si la fila está vacía
                if (currentRow.getCell(0) == null || getCellValue(currentRow, 0).isEmpty()) {
                    break;
                }

                Aprendiz aprendiz = new Aprendiz();

                // Nombres
                aprendiz.setNombres(getCellValue(currentRow, 0));

                // Apellidos
                aprendiz.setApellidos(getCellValue(currentRow, 1));

                // Tipo Doc
                aprendiz.setTipoDocumento(getCellValue(currentRow, 2));

                // Número Doc
                String numDoc = getCellValue(currentRow, 3)
                        .replace(".", "")
                        .replace(",", "")
                        .trim();
                aprendiz.setNumeroDocumento(numDoc);

                // Fecha Nacimiento string con formato yyyy-MM-dd :D
                String fechaTexto = getCellValue(currentRow, 4);
                try {
                    aprendiz.setFechaNacimiento(LocalDate.parse(fechaTexto));
                } catch (DateTimeParseException e) {
                    throw new RuntimeException(
                        "Formato de fecha inválido en fila " + (rowNumber + 1)
                        + ". Debe ser yyyy-MM-dd. Se recibió: " + fechaTexto
                    );
                }

                // Correo
                aprendiz.setCorreo(getCellValue(currentRow, 5));

                // Celular osea el bicho siuuu
                aprendiz.setCelular(getCellValue(currentRow, 6));

                // Etapa formación
                aprendiz.setEtapaFormacion(getCellValue(currentRow, 7));

                // Código Ficha
                String codigoFicha = getCellValue(currentRow, 8);
                Ficha ficha = fichaRepository.findByCodigo(codigoFicha);

                if (ficha == null) {
                    throw new RuntimeException(
                        "Ficha no encontrada: " + codigoFicha +
                        " en la fila " + (rowNumber + 1)
                    );
                }

                aprendiz.setFicha(ficha);

                aprendices.add(aprendiz);
                rowNumber++;
            }

            workbook.close();
            return aprendices;

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar Excel: " + e.getMessage());
        }
    }

    
    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
