package daos.people;

import models.people.User;

import java.util.Optional;

public interface UserDAO<T> extends PersonDAO<T>
{
    Optional<User> findByEmail(String email);
}