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
        
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public void guardar(Taller taller) {
        // VALIDACIÓN DE HORARIO (9:00 AM - 4:00 PM)
        if (taller.getHoraInicio() != null && taller.getHoraFin() != null) {
            
            LocalTime inicioPermitido = LocalTime.of(9, 0);
            LocalTime finPermitido = LocalTime.of(17, 0); 

            if (taller.getHoraInicio().isBefore(inicioPermitido) || taller.getHoraInicio().isAfter(finPermitido)) {
                throw new RuntimeException("La hora de inicio debe estar entre 9:00 AM y 5:00 PM.");
            }
            if (taller.getHoraFin().isBefore(inicioPermitido) || taller.getHoraFin().isAfter(finPermitido)) {
                throw new RuntimeException("La hora de fin debe estar entre 9:00 AM y 5:00 PM.");
            }

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

    // NUEVA LÓGICA DE ESTADO
    @Override
    public void cambiarEstado(Long id) {
        Taller taller = repository.findById(id).orElse(null);
        if (taller != null) {
            taller.setActivo(!taller.isActivo());
            repository.save(taller);
        }
    }
}