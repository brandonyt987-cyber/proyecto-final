package com.sena.sistemaintegralsena.config;

import com.sena.sistemaintegralsena.entity.*;
import com.sena.sistemaintegralsena.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.Normalizer; // Importante para quitar tildes
import java.time.LocalDate;
import java.util.Optional;

@Component
public class DataSeeder {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CoordinacionRepository coordinacionRepository;
    @Autowired private InstructorRepository instructorRepository;
    @Autowired private FichaRepository fichaRepository;
    @Autowired private AprendizRepository aprendizRepository;
    @Autowired private VoceroRepository voceroRepository;

    private final String[] LISTA_COORDINACIONES = {
        "Teleinformática y Sistemas",
        "Gestión Administrativa",
        "Financiera y Contable",
        "Talento Humano",
        "Mercadeo y Logística",
        "Gastronomía y Turismo",
        "Electricidad y Electrónica",
        "Construcción e Infraestructura"
    };

    private Rol findOrCreateRol(String nombre) {
        Optional<Rol> rolOpt = rolRepository.findByNombre(nombre);
        if (rolOpt.isPresent()) return rolOpt.get();

        Rol nuevo = new Rol();
        nuevo.setNombre(nombre);
        return rolRepository.save(nuevo);
    }

    private String generarCelularColombiano() {
        String[] prefijos = {"310", "311", "312", "313", "314", "315", "316", "317", "318", "319", "320"};
        String pref = prefijos[(int) (Math.random() * prefijos.length)];
        StringBuilder numero = new StringBuilder(pref);
        for (int i = 0; i < 7; i++) {
            numero.append((int) (Math.random() * 10));
        }
        return numero.toString();
    }

    private LocalDate calcularFechaNacimiento(int edad) {
        int year = LocalDate.now().getYear() - edad;
        int dayOfYear = 1 + (int)(Math.random() * 365);
        return LocalDate.ofYearDay(year, dayOfYear);
    }

    private String obtenerTipoDocumento(int edad) {
        if (edad < 18) {
            String[] tipos = {"TI", "TI", "TI", "TI", "TI"};
            return tipos[(int) (Math.random() * tipos.length)];
        } else {
            String[] tipos = {"CC", "CE", "CC", "CC", "CC", "CE", "PEP"};
            return tipos[(int) (Math.random() * tipos.length)];
        }
    }

    // MÉTODO PARA LIMPIAR TILDES Y Ñ EN CORREOS
    private String limpiarParaCorreo(String texto) {
        if (texto == null) return "";
        // Normaliza el texto para separar los acentos de las letras (ej: á -> a + ´)
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        // Elimina los caracteres de acento y convierte a minúsculas
        return normalizado.replaceAll("\\p{M}", "").toLowerCase();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedData() {

        // ============================
        // ROLES Y USUARIOS BASE
        // ============================
        Rol rolAdmin = findOrCreateRol("ADMIN");
        Rol rolPsico = findOrCreateRol("PSICOLOGA");
        Rol rolSocial = findOrCreateRol("T_SOCIAL");

        if (!usuarioRepository.existsByEmail("admin1@sena.edu.co")) {
            Usuario u = new Usuario();
            u.setNombre("Administrador Lider");
            u.setEmail("admin1@sena.edu.co");
            u.setPassword(passwordEncoder.encode("Nala123*"));
            u.setEnabled(true);
            u.setRol(rolAdmin.getNombre());
            usuarioRepository.save(u);
        }

        if (!usuarioRepository.existsByEmail("psico@sena.edu.co")) {
            Usuario u = new Usuario();
            u.setNombre("Laura Gómez");
            u.setEmail("psico@sena.edu.co");
            u.setPassword(passwordEncoder.encode("Nala123*"));
            u.setEnabled(true);
            u.setRol(rolPsico.getNombre());
            usuarioRepository.save(u);
        }

        if (!usuarioRepository.existsByEmail("social@sena.edu.co")) {
            Usuario u = new Usuario();
            u.setNombre("María Torres");
            u.setEmail("social@sena.edu.co");
            u.setPassword(passwordEncoder.encode("Nala123*"));
            u.setEnabled(true);
            u.setRol(rolSocial.getNombre());
            usuarioRepository.save(u);
        }

        // =====================================
        // LISTAS DE DATOS (Con Tildes para visualización correcta en Nombre)
        // =====================================
        
        String[] nombresInstructores = {
            "Andrés Felipe", "Claudia María", "Jorge Enrique", "Diana Marcela",
            "Ricardo José", "Patricia Elena", "Julián David", "Paola Andrea"
        };
        String[] apellidosInstructores = {
            "Ramírez Pérez", "Ospina Giraldo", "Martínez López", "Gutiérrez Vega",
            "Salazar Ríos", "Arango Henao", "Patiño Correa", "Castaño Gil"
        };

        String[] nombresAprendices = {
            "Juan Camilo", "Valentina", "Santiago", "Daniela", "Mateo", "Camila",
            "Samuel", "Isabella", "David", "Laura Sofía", "Alejandro", "Mariana",
            "Nicolás", "Gabriela", "Sebastián", "Valeria", "Diego Andrés", "Lucía",
            "Fernando", "Elena María", "Jorge Luis", "Mónica", "Esteban", "Carolina"
        };

        String[] apellidosAprendices = {
            "Gómez Hernández", "Rojas Díaz", "Cortés Morales", "Vargas Suárez", "López Pinto", "Ruiz Castro",
            "Medina Herrera", "Jiménez Silva", "Romero Mendoza", "Aguilar Delgado", "Peña Rivas", "Cabrera Solano",
            "Ortiz Valencia", "Muñoz Restrepo", "Sánchez Cárdenas", "Torres Guzmán", "Navarro Bernal", "Acosta Marín",
            "Mejía Tovar", "León Pardo", "Vega Cruz", "Molina Reyes", "Pineda Osorio", "Campos Trujillo"
        };

        // =====================================
        // EJECUCIÓN
        // =====================================

        if (coordinacionRepository.count() == 0) {

            System.out.println("\n INICIANDO CARGA MASIVA (Correos sin tildes)...\n");

            int indexInstructor = 0;
            int indexAprendizGlobal = 0;
            int contador = 1;
            int contadorMenores = 0; 

            for (String area : LISTA_COORDINACIONES) {

                // 1. COORDINACIÓN
                Coordinacion coord = new Coordinacion();
                coord.setNombre(area);
                coord = coordinacionRepository.save(coord);

                // 2. INSTRUCTOR
                Instructor inst = new Instructor();
                inst.setNombres(nombresInstructores[indexInstructor]);
                inst.setApellidos(apellidosInstructores[indexInstructor]);
                inst.setTipoDocumento(obtenerTipoDocumento(30)); 
                inst.setNumeroDocumento("7000" + contador + "21");
                inst.setProfesion("Especialista en " + area);
                
                // Generación de correo limpio para Instructor
                String apellidoLimpio = limpiarParaCorreo(apellidosInstructores[indexInstructor].split(" ")[0]);
                inst.setCorreo("instructor." + apellidoLimpio + "@sena.edu.co");
                
                inst.setTelefono(generarCelularColombiano());
                inst.setCoordinacion(coord);
                instructorRepository.save(inst);

                // 3. FICHA
                Ficha ficha = new Ficha();
                ficha.setCodigo("29000" + contador);
                ficha.setPrograma("Tecnólogo en " + area);
                ficha.setJornada("Diurna");
                ficha.setModalidad("Presencial");
                ficha.setCoordinacion(coord);
                ficha = fichaRepository.save(ficha);

                // 4. APRENDICES
                Aprendiz voceroSeleccionado = null;

                for (int i = 0; i < 3; i++) {
                    Aprendiz ap = new Aprendiz();
                    
                    ap.setNombres(nombresAprendices[indexAprendizGlobal]);
                    ap.setApellidos(apellidosAprendices[indexAprendizGlobal]);
                    
                    // Lógica de Edad
                    int edadAsignada;
                    if (contadorMenores < 3) {
                        edadAsignada = 16 + (int)(Math.random() * 2);
                        contadorMenores++;
                    } else {
                        edadAsignada = 20 + (int)(Math.random() * 7);
                    }
                    ap.setFechaNacimiento(calcularFechaNacimiento(edadAsignada));
                    ap.setTipoDocumento(obtenerTipoDocumento(edadAsignada));
                    ap.setNumeroDocumento("100" + contador + i + "51");
                    
                    // Generación de correo limpio para Aprendiz
                    String nombreLimpio = limpiarParaCorreo(nombresAprendices[indexAprendizGlobal].split(" ")[0]);
                    String apellidoApLimpio = limpiarParaCorreo(apellidosAprendices[indexAprendizGlobal].split(" ")[0]);
                    ap.setCorreo(nombreLimpio + "." + apellidoApLimpio + "@sena.edu.co");

                    ap.setCelular(generarCelularColombiano());
                    ap.setEtapaFormacion("Lectiva");
                    ap.setFicha(ficha);
                    
                    ap = aprendizRepository.save(ap);

                    if (i == 0) voceroSeleccionado = ap;
                    
                    indexAprendizGlobal++;
                }

                // 5. VOCERO
                Vocero voc = new Vocero();
                voc.setAprendiz(voceroSeleccionado);
                voc.setRazonCambio("Designación inicial automática.");
                voceroRepository.save(voc);

                indexInstructor++;
                contador++;

                System.out.println("✔️ Generado: " + area);
            }

            System.out.println("\n FINALIZADO: Carga completada. Correos validados sin tildes.\n");
        }
    }
}