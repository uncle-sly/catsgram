package ru.yandex.practicum.catsgram.service;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.NewUserRequest;
import ru.yandex.practicum.catsgram.dto.UpdateUserRequest;
import ru.yandex.practicum.catsgram.dto.UserDto;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.UserMapper;
import ru.yandex.practicum.catsgram.model.User;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
//    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
//        return new ArrayList<>(users.values());
        return userRepository.findAll();
    }

    public UserDto createUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        Optional<User> alreadyExistUser = userRepository.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }

        User user = UserMapper.mapToUser(request);

        user = userRepository.save(user);

        return UserMapper.mapToUserDto(user);
    }

/*  public User create(User user) {
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
    }*/

    /* public User update(User newUser) {
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
    } */

/* private Long getNextId() {
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
    } */

    public UserDto getUserById(long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    /*   public Optional<User> findUserById(Long id) {
            return Optional.ofNullable(users.get(id));
        } */

    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(long userId, UpdateUserRequest request) {
        User updatedUser = userRepository.findById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userRepository.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

}

