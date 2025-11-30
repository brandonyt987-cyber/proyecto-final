package com.sena.sistemaintegralsena.service.impl;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.entity.Comite;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.repository.AprendizRepository;
import com.sena.sistemaintegralsena.repository.ComiteRepository;
import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import com.sena.sistemaintegralsena.repository.VoceroRepository;
import com.sena.sistemaintegralsena.service.ComiteService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class ComiteServiceImpl implements ComiteService {

    private final ComiteRepository comiteRepository;
    private final AprendizRepository aprendizRepository;
    private final UsuarioRepository usuarioRepository;
    private final VoceroRepository voceroRepository;

    public ComiteServiceImpl(ComiteRepository comiteRepository, 
                             AprendizRepository aprendizRepository,
                             UsuarioRepository usuarioRepository,
                             VoceroRepository voceroRepository) {
        this.comiteRepository = comiteRepository;
        this.aprendizRepository = aprendizRepository;
        this.usuarioRepository = usuarioRepository;
        this.voceroRepository = voceroRepository;
    }

    @Override
    public List<Comite> listarTodos() {
        return comiteRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public void guardar(Comite comite, Long aprendizId, String emailProfesional) {
        
        // 1. VALIDACIÓN BLOQUEANTE: SI ES VOCERO, NO DEJA GUARDAR (Solo si es nuevo)
        // Se agrega comite.getId() == null para permitir editar comités existentes sin bloquearse
        if (comite.getId() == null && voceroRepository.existsByAprendizId(aprendizId)) {
            throw new RuntimeException("⚠️ El aprendiz seleccionado es VOCERO ACTIVO. Debe realizar el cambio de vocería antes de citarlo a comité.");
        }

        // 2. Validaciones de Fecha
        LocalDate today = LocalDate.now();
        LocalDate fechaComite = comite.getFecha();

        int mesValido = (today.getDayOfMonth() > 20) ? today.plusMonths(1).getMonthValue() : today.getMonthValue();
        int anioValido = (today.getDayOfMonth() > 20) ? today.plusMonths(1).getYear() : today.getYear();

        if (fechaComite.getMonthValue() != mesValido || fechaComite.getYear() != anioValido) {
            throw new RuntimeException("Fecha fuera de rango. Corte día 20.");
        }
        if (fechaComite.getDayOfMonth() > 20) {
            throw new RuntimeException("Solo se programan hasta el día 20.");
        }
        if (fechaComite.isBefore(today)) {
            throw new RuntimeException("No puede ser en el pasado.");
        }

        // 🔑 CORRECCIÓN AQUÍ: Validación de Hora Segura (Evita NullPointer)
        LocalTime hora = comite.getHora();
        if (hora == null) {
             throw new RuntimeException("Error: La hora es obligatoria.");
        }
        if (hora.isBefore(LocalTime.of(9, 0)) || hora.isAfter(LocalTime.of(17, 0))) {
            throw new RuntimeException("Horario no permitido (9:00 AM - 5:00 PM).");
        }

        // 3. Validación Fecha Plazo
        LocalDate plazo = comite.getFechaPlazo();
        if (plazo.isBefore(today)) throw new RuntimeException("Plazo inválido (pasado).");
        if (plazo.isAfter(today.plusMonths(1))) throw new RuntimeException("Plazo máximo 1 mes.");

        // 4. Asociaciones
        Aprendiz aprendiz = aprendizRepository.findById(aprendizId)
                .orElseThrow(() -> new RuntimeException("Aprendiz no encontrado"));
        comite.setAprendiz(aprendiz);

        if (aprendiz.getFicha() != null && aprendiz.getFicha().getCoordinacion() != null) {
            comite.setCoordinacion(aprendiz.getFicha().getCoordinacion());
        } else {
            throw new RuntimeException("Ficha sin coordinación asignada.");
        }

        Usuario profesional = usuarioRepository.findByEmail(emailProfesional)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        comite.setProfesional(profesional); 

        comiteRepository.save(comite);
    }

    @Override
    @Transactional(readOnly = true)
    public Comite buscarPorId(Long id) {
        return comiteRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        comiteRepository.deleteById(id);
    }
}