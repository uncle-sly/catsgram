package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        if ((user.getEmail() == null) || (user.getEmail().isBlank())) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (checkEmail(user)) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (!oldUser.getEmail().equals(newUser.getEmail()) && (checkEmail(newUser))) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            if (newUser.getUsername() == null && newUser.getPassword() == null && newUser.getEmail() == null) {
                return oldUser;
            }
            oldUser.setUsername(newUser.getUsername());
            oldUser.setPassword(newUser.getPassword());
            oldUser.setEmail(newUser.getEmail());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с ID " + newUser.getId() + " не найден.");

    }

    private Long getNextId() {
        long userMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++userMaxId;
    }

    private boolean checkEmail(User user) {
        return users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(u -> u.equals(user.getEmail()));
    }

}

