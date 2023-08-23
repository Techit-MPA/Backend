package com.springles.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGameRecord is a Querydsl query type for GameRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGameRecord extends EntityPathBase<GameRecord> {

    private static final long serialVersionUID = 453233842L;

    public static final QGameRecord gameRecord = new QGameRecord("gameRecord");

    public final NumberPath<Long> capacity = createNumber("capacity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> duration = createDateTime("duration", java.time.LocalDateTime.class);

    public final NumberPath<Long> head = createNumber("head", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath open = createBoolean("open");

    public final NumberPath<Long> ownerId = createNumber("ownerId", Long.class);

    public final StringPath state = createString("state");

    public final StringPath title = createString("title");

    public final BooleanPath winner = createBoolean("winner");

    public QGameRecord(String variable) {
        super(GameRecord.class, forVariable(variable));
    }

    public QGameRecord(Path<? extends GameRecord> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGameRecord(PathMetadata metadata) {
        super(GameRecord.class, metadata);
    }

}

