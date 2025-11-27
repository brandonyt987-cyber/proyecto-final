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

    public ComiteServiceImpl(ComiteRepository comiteRepository, AprendizRepository aprendizRepository, UsuarioRepository usuarioRepository, VoceroRepository voceroRepository) {
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
        
        // 1. VALIDACIÓN VOCERO
        if (comite.getId() == null && voceroRepository.existsByAprendizId(aprendizId)) {
            throw new RuntimeException("EL APRENDIZ ES VOCERO. Debe cambiar la vocería primero.");
        }

        // 2. VALIDACIÓN DE HORA (Solo verificamos que no sea nula)
        LocalTime hora = comite.getHora();
        if (hora == null) {
             throw new RuntimeException("La hora es obligatoria.");
        }
        
        // 3. VALIDACIÓN DE FECHAS (Resto de lógica)
        LocalDate today = LocalDate.now();
        LocalDate fechaComite = comite.getFecha();
        
        if (fechaComite.isBefore(today)) throw new RuntimeException("La fecha del comité no puede ser en el pasado.");
        
        if (fechaComite.getDayOfMonth() > 20) throw new RuntimeException("Solo se pueden programar comités hasta el día 20 del mes.");
        
        // Validaciones Plazo (Manuales)
        LocalDate plazo = comite.getFechaPlazo();
        if (plazo != null) {
             if (plazo.isBefore(today)) throw new RuntimeException("Plazo inválido.");
             if (plazo.isAfter(today.plusMonths(1))) throw new RuntimeException("Plazo máximo 1 mes.");
        }

        // 4. ASOCIACIONES
        Aprendiz aprendiz = aprendizRepository.findById(aprendizId).orElseThrow(() -> new RuntimeException("Aprendiz no encontrado"));
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
    public Comite buscarPorId(Long id) {
        return comiteRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        comiteRepository.deleteById(id);
    }
}