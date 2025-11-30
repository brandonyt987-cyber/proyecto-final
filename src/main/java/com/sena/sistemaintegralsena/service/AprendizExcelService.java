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
import java.time.ZoneId;
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

                if (rowNumber == 0) { // Saltar cabecera
                    rowNumber++;
                    continue;
                }
                
                // Validación básica de fila vacía
                if (currentRow.getCell(0) == null || currentRow.getCell(0).getStringCellValue().isEmpty()) {
                    break;
                }

                Aprendiz aprendiz = new Aprendiz();

                // 0: Nombres, 1: Apellidos
                aprendiz.setNombres(getCellValue(currentRow, 0));
                aprendiz.setApellidos(getCellValue(currentRow, 1));
                
                // 2: Tipo Doc, 3: Num Doc
                aprendiz.setTipoDocumento(getCellValue(currentRow, 2));
                String doc = getCellValue(currentRow, 3);
                aprendiz.setNumeroDocumento(doc.replace(".", "").replace(",", ""));

                // --- 4: FECHA DE NACIMIENTO (NUEVO) ---
                // Lógica especial para leer fechas de Excel
                Cell dateCell = currentRow.getCell(4);
                if (dateCell != null && DateUtil.isCellDateFormatted(dateCell)) {
                    LocalDate fecha = dateCell.getDateCellValue().toInstant()
                                      .atZone(ZoneId.systemDefault()).toLocalDate();
                    aprendiz.setFechaNacimiento(fecha);
                } else {
                    // Manejo de error si la fecha no es válida o es texto
                    throw new RuntimeException("Formato de fecha inválido en fila " + (rowNumber + 1) + ". Use formato Fecha en Excel.");
                }

                // 5: Correo, 6: Celular, 7: Etapa
                aprendiz.setCorreo(getCellValue(currentRow, 5));
                aprendiz.setCelular(getCellValue(currentRow, 6));
                aprendiz.setEtapaFormacion(getCellValue(currentRow, 7));

                // 8: Código Ficha (Desplazado)
                String codigoFicha = getCellValue(currentRow, 8);
                Ficha ficha = fichaRepository.findByCodigo(codigoFicha);
                
                if (ficha == null) {
                    throw new RuntimeException("Ficha no encontrada: " + codigoFicha + " en la fila " + (rowNumber + 1));
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
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}