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

                // Saltar cabecera
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                // Evitar filas vacías
                if (currentRow.getCell(0) == null || currentRow.getCell(0).getStringCellValue().isEmpty()) {
                    break;
                }

                Aprendiz aprendiz = new Aprendiz();

                // Asumiendo orden de columnas: 
                // 0:Nombres, 1:Apellidos, 2:TipoDoc, 3:NumDoc, 4:Correo, 5:Celular, 6:Etapa, 7:CodigoFicha
                
                aprendiz.setNombres(getCellValue(currentRow, 0));
                aprendiz.setApellidos(getCellValue(currentRow, 1));
                aprendiz.setTipoDocumento(getCellValue(currentRow, 2));
                
                // Convertir documento a string sin decimales si viene como número
                String doc = getCellValue(currentRow, 3);
                aprendiz.setNumeroDocumento(doc.replace(".", "").replace(",", ""));
                
                aprendiz.setCorreo(getCellValue(currentRow, 4));
                aprendiz.setCelular(getCellValue(currentRow, 5));
                aprendiz.setEtapaFormacion(getCellValue(currentRow, 6));

                // Buscar Ficha por Código
                String codigoFicha = getCellValue(currentRow, 7);
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

    // Método auxiliar para evitar NullPointer y manejar tipos de celda
    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        
        // Forzar lectura como String
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}