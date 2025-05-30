package ru.alexgur.intershop.system.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import ru.alexgur.intershop.system.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BaseRepository<T> {
    private final JdbcOperations jdbc;
    private final RowMapper<T> mapper;
    private final NamedParameterJdbcTemplate njdbc;

    public BaseRepository(NamedParameterJdbcTemplate njdbc, RowMapper<T> mapper) {
        this.njdbc = njdbc;
        this.mapper = mapper;
        jdbc = njdbc.getJdbcOperations();
    }

    public boolean checkIdExist(String query, Long id) {
        return getNumberOrZeroImpl(query, id) != 0;
    }

    public long getNumberOrZero(String query, SqlParameterSource params) {
        return getNumberOrZeroImpl(query, params);
    }

    public long getNumberOrZero(String query, Object... params) {
        return getNumberOrZeroImpl(query, params);
    }

    public Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new InternalServerException("Ошибка при поиске записи в базе данных");
        }
    }

    public List<T> findMany(String query, Object... params) {
        try {
            return njdbc.getJdbcOperations().query(query, mapper, params);
        } catch (DataAccessException e) {
            throw new InternalServerException("Ошибка при получении списка записей из базы данных");
        }
    }

    public List<T> findMany(String query, SqlParameterSource params) {
        try {
            return njdbc.query(query, params, mapper);
        } catch (DataAccessException e) {
            // throw new InternalServerException("Ошибка при получении списка записей из
            // базы данных");
            throw new RuntimeException("Ошибка при получении списка записей" + e.getMessage(), e);
        }
    }

    public List<Long> findManyIds(String query, Object... params) {
        try {
            return jdbc.query(query, (rs, rowNum) -> rs.getLong(1), params);
        } catch (DataAccessException e) {
            throw new InternalServerException("Ошибка при получении id записи в базе данных");
        }
    }

    public <V> List<V> findManyIdToId(String query, RowMapper<V> pairMapper, SqlParameterSource params) {
        try {
            return njdbc.query(query, params, pairMapper);
        } catch (DataAccessException e) {
            throw new InternalServerException("Ошибка при получении IdToId");
        }
    }

    public Long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        List<Map<String, Object>> keys = keyHolder.getKeyList();
        int countKeys = keys.size();

        if (countKeys > 1) {
            return (Long) keys.get(0).entrySet().iterator().next().getValue();
        } else if (countKeys == 1) {
            return (Long) keys.get(0).entrySet().iterator().next().getValue();
        }
        throw new InternalServerException("Не удалось сохранить данные");
    }

    public void insertMany(String query, SqlParameterSource... params) {
        try {
            njdbc.batchUpdate(query, params);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка добавления множества данных: " +
                    e.getMessage(), e);
        }
    }

    public boolean delete(String query, long id) {
        int rowsDeleted = jdbc.update(query, id);
        return rowsDeleted > 0;
    }

    public void update(String query, Object... params) {
        jdbc.update(query, params);
    }

    private Long getNumberOrZeroImpl(String query, Object... params) {
        try {
            Long count;
            if (params == null) {
                count = jdbc.queryForObject(query, Long.class);
            } else {
                count = jdbc.queryForObject(query, Long.class, params);
            }
            return count != null ? count : 0L;
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    private Long getNumberOrZeroImpl(String query, SqlParameterSource params) {
        try {
            return Optional.ofNullable(njdbc.queryForObject(query, params, Long.class))
                    .orElse(0L);
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
