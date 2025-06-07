package com.project.practica1.logic.entity.user;

import com.project.practica1.logic.entity.rol.Role;
import com.project.practica1.logic.entity.rol.RoleEnum;
import com.project.practica1.logic.entity.rol.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserSeeder(
            RoleRepository roleRepository,
            UserRepository  userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.crearUsuarioNormal();
        this.crearSuperAdmin();
    }

    private void crearUsuarioNormal() {

        User normalUser = new User();
        normalUser.setName("ana");
        normalUser.setLastname("arias");
        normalUser.setEmail("ana@gmail.com");
        normalUser.setPassword("1234");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        Optional<User> optionalUser = userRepository.findByEmail(normalUser.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var user = new User();
        user.setName(normalUser.getName());
        user.setLastname(normalUser.getLastname());
        user.setEmail(normalUser.getEmail());
        user.setPassword(passwordEncoder.encode(normalUser.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
    }

    private void crearSuperAdmin() {

        User adminUser = new User();
        adminUser.setName("luis");
        adminUser.setLastname("jara");
        adminUser.setEmail("luis@gmail.com");
        adminUser.setPassword("1234");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN_ROLE);
        Optional<User> optionalUser = userRepository.findByEmail(adminUser.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var user = new User();
        user.setName(adminUser.getName());
        user.setLastname(adminUser.getLastname());
        user.setEmail(adminUser.getEmail());
        user.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
    }

}
