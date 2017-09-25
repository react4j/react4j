package gwt.react.todo_mvc.client;

import elemental2.dom.DomGlobal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.realityforge.arez.spy.ActionCompletedEvent;
import org.realityforge.arez.spy.ActionStartedEvent;
import org.realityforge.arez.spy.ComputeCompletedEvent;
import org.realityforge.arez.spy.ComputeStartedEvent;
import org.realityforge.arez.spy.ComputedValueActivatedEvent;
import org.realityforge.arez.spy.ComputedValueCreatedEvent;
import org.realityforge.arez.spy.ComputedValueDeactivatedEvent;
import org.realityforge.arez.spy.ComputedValueDisposedEvent;
import org.realityforge.arez.spy.ObservableChangedEvent;
import org.realityforge.arez.spy.ObservableCreatedEvent;
import org.realityforge.arez.spy.ObservableDisposedEvent;
import org.realityforge.arez.spy.ObserverCreatedEvent;
import org.realityforge.arez.spy.ObserverDisposedEvent;
import org.realityforge.arez.spy.ObserverErrorEvent;
import org.realityforge.arez.spy.ReactionCompletedEvent;
import org.realityforge.arez.spy.ReactionScheduledEvent;
import org.realityforge.arez.spy.ReactionStartedEvent;
import org.realityforge.arez.spy.TransactionCompletedEvent;
import org.realityforge.arez.spy.TransactionStartedEvent;

final class SpyUtil
{
  private static final Map<Class, EventEmitter> _emitterMap = new HashMap<>();
  private static int _indentLevel;

  private SpyUtil()
  {
  }

  static
  {
    emitter( ObserverCreatedEvent.class,
             IndentType.NONE,
             e -> "Observer Created " + e.getObserver().getName() );
    emitter( ObserverCreatedEvent.class,
             IndentType.NONE,
             e -> "Observer Created " + e.getObserver().getName() );
    emitter( ObserverDisposedEvent.class,
             IndentType.NONE,
             e -> "Observer Disposed " + e.getObserver().getName() );
    emitter( ObserverErrorEvent.class,
             IndentType.NONE,
             e -> "Observer Error " + e.getObserver().getName() + " " + e.getError() + " " + e.getThrowable() );
    emitter( ObservableCreatedEvent.class,
             IndentType.NONE,
             e -> "Observable Created " + e.getObservable().getName() );
    emitter( ObservableDisposedEvent.class,
             IndentType.NONE,
             e -> "Observable Disposed " + e.getObservable().getName() );
    emitter( ObservableChangedEvent.class,
             IndentType.NONE,
             e -> "Observable Changed " + e.getObservable().getName() );
    emitter( ComputedValueActivatedEvent.class,
             IndentType.NONE,
             e -> "Computed Value Activate " + e.getComputedValue().getName() );
    emitter( ComputedValueDeactivatedEvent.class,
             IndentType.NONE,
             e -> "Computed Value Deactivate " + e.getComputedValue().getName() );
    emitter( ComputedValueCreatedEvent.class,
             IndentType.NONE,
             e -> "Computed Value Created " + e.getComputedValue().getName() );
    emitter( ComputedValueDisposedEvent.class,
             IndentType.NONE,
             e -> "Computed Value Disposed " + e.getComputedValue().getName() );
    emitter( ReactionStartedEvent.class,
             IndentType.IN,
             e -> "Reaction Started " + e.getObserver().getName() );
    emitter( ReactionScheduledEvent.class,
             IndentType.NONE,
             e -> "Reaction Scheduled " + e.getObserver().getName() );
    emitter( ReactionCompletedEvent.class,
             IndentType.OUT,
             e -> "Reaction Completed " + e.getObserver().getName() + " [" + e.getDuration() + "]" );
    emitter( TransactionStartedEvent.class,
             IndentType.IN,
             e -> "Transaction Started " +
                  e.getName() +
                  " Mutation=" +
                  e.isMutation() +
                  " Tracker=" +
                  ( e.getTracker() == null ? null : e.getTracker().getName() ) );
    emitter( TransactionCompletedEvent.class,
             IndentType.OUT,
             e -> "Transaction Completed " +
                  e.getName() +
                  " Mutation=" +
                  e.isMutation() +
                  " Tracker=" +
                  ( e.getTracker() == null ? null : e.getTracker().getName() ) + " [" + e.getDuration() + "]" );
    emitter( ComputeStartedEvent.class,
             IndentType.IN,
             e -> "Compute Started " + e.getComputedValue().getName() );
    emitter( ComputeCompletedEvent.class,
             IndentType.OUT,
             e -> "Compute Completed " + e.getComputedValue().getName() + " [" + e.getDuration() +
                  "]" );

    emitter( ActionStartedEvent.class,
             IndentType.IN,
             e -> "Action Started " +
                  e.getName() +
                  "(" +
                  Arrays.toString( e.getParameters() ) +
                  ")" );
    emitter( ActionCompletedEvent.class,
             IndentType.OUT,
             e -> "Action Completed " +
                  e.getName() +
                  "(" +
                  Arrays.toString( e.getParameters() ) +
                  ")" + ( e.isExpectsResult() && null == e.getThrowable() ? " = " + e.getResult() : "" ) +
                  ( null != e.getThrowable() ? "threw " + e.getThrowable() : "" ) +
                  " Duration [" +
                  e.getDuration() +
                  "]" );
  }

  static <T> void emitter( @Nonnull final Class<T> type,
                           @Nonnull final IndentType indentType,
                           @Nonnull final Function<T, String> processor )
  {
    _emitterMap.put( type, new EventEmitter<>( type, indentType, processor ) );
  }

  @SuppressWarnings( "unchecked" )
  static void emitEvent( @Nonnull final Object event )
  {
    final EventEmitter emitter = _emitterMap.get( event.getClass() );
    if ( null != emitter )
    {
      _indentLevel += emitter.getIndentType() == IndentType.OUT ? -1 : 0;

      final StringBuilder sb = new StringBuilder();
      for ( int i = 0; i < _indentLevel; i++ )
      {
        sb.append( "  " );
      }
      sb.append( emitter.getProcessor().apply( event ) );

      DomGlobal.console.log( sb.toString() );
      _indentLevel += emitter.getIndentType() == IndentType.IN ? 1 : 0;
    }
    else
    {
      DomGlobal.console.log( event );
    }
  }

  enum IndentType
  {
    IN,
    OUT,
    NONE
  }

  private static class EventEmitter<T>
  {
    private final Class<T> _type;
    private final IndentType _indentType;
    private final Function<T, String> _processor;

    EventEmitter( @Nonnull final Class<T> type,
                  @Nonnull final IndentType indentType,
                  @Nonnull final Function<T, String> processor )
    {
      _type = Objects.requireNonNull( type );
      _indentType = Objects.requireNonNull( indentType );
      _processor = Objects.requireNonNull( processor );
    }

    @Nonnull
    Class<T> getType()
    {
      return _type;
    }

    @Nonnull
    IndentType getIndentType()
    {
      return _indentType;
    }

    @Nonnull
    Function<T, String> getProcessor()
    {
      return _processor;
    }
  }
}
