package at.uni.innsbruck.htibot.core.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Optional;

public interface UpdateCreateHolder extends Serializable {

  Optional<ZonedDateTime> getCreatedAt();

  Optional<ZonedDateTime> getUpdatedAt();

}
