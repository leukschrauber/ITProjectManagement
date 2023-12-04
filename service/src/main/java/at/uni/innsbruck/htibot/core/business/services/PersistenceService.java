package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.IdHolder;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface PersistenceService<T extends IdHolder<V>, V extends Comparable<V>> extends
    Serializable {

  Optional<T> getById(final V id);

  @NotNull <W extends T> W save(@NotNull W entity) throws PersistenceException;

  @NotNull <W extends T> W update(@NotNull W entity) throws PersistenceException;

  @NotNull <W extends T> W delete(@NotNull W entity) throws PersistenceException;

  @NotNull <W extends T> W reload(@NotNull W entity);

  @NotNull <W extends T> W reloadDetached(@NotNull W entity);

  @NotNull <W extends T, C extends Collection<W>> C reload(@NotNull Collection<W> entities,
      @NotNull C resultCollection);

  @NotNull <W extends T, C extends Collection<W>> C reloadDetached(@NotNull Collection<W> entities,
      @NotNull C resultCollection);

}
