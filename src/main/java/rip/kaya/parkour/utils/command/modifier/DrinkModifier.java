package rip.kaya.parkour.utils.command.modifier;

import rip.kaya.parkour.utils.command.parametric.CommandParameter;
import rip.kaya.parkour.utils.command.command.CommandExecution;
import rip.kaya.parkour.utils.command.exception.CommandExitMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface DrinkModifier<T> {

    Optional<T> modify(@Nonnull CommandExecution execution, @Nonnull CommandParameter commandParameter, @Nullable T argument) throws CommandExitMessage;

}
