package com.sena.sistemaintegralsena.service.impl;

import com.sena.sistemaintegralsena.entity.Taller;
import com.sena.sistemaintegralsena.repository.TallerRepository;
import com.sena.sistemaintegralsena.service.TallerService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class TallerServiceImpl implements TallerService {

    private final TallerRepository repository;

    public TallerServiceImpl(TallerRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Taller> listarTodos() {
        // La lista sigue ordenada por fecha descendente (más nuevo primero)
        return repository.findAll(Sort.by(Sort.Direction.DESC, "fecha"));
    }

    @Override
    public void guardar(Taller taller) {
        // VALIDACIÓN DE HORARIO (9:00 AM - 4:00 PM)
        if (taller.getHoraInicio() != null && taller.getHoraFin() != null) {
            
            LocalTime inicioPermitido = LocalTime.of(9, 0);
            LocalTime finPermitido = LocalTime.of(16, 0);

            // 1. Validar que estén dentro del rango
            if (taller.getHoraInicio().isBefore(inicioPermitido) || taller.getHoraInicio().isAfter(finPermitido)) {
                
                throw new RuntimeException("La hora de inicio debe estar entre 9:00 AM y 4:00 PM.");
            }
            if (taller.getHoraFin().isBefore(inicioPermitido) || taller.getHoraFin().isAfter(finPermitido)) {
                
                throw new RuntimeException("La hora de fin debe estar entre 9:00 AM y 4:00 PM.");
            }

            // 2. Validar coherencia (Fin > Inicio)
            if (taller.getHoraFin().isBefore(taller.getHoraInicio())) {
                throw new RuntimeException("La hora de fin no puede ser anterior a la hora de inicio.");
            }
            if (taller.getHoraFin().equals(taller.getHoraInicio())) {
                throw new RuntimeException("El taller debe tener una duración válida.");
            }
        }
        
        repository.save(taller);
    }

    @Override
    @Transactional(readOnly = true)
    public Taller buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}