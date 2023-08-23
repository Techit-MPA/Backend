package com.springles.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberGameInfo is a Querydsl query type for MemberGameInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberGameInfo extends EntityPathBase<MemberGameInfo> {

    private static final long serialVersionUID = -872909111L;

    public static final QMemberGameInfo memberGameInfo = new QMemberGameInfo("memberGameInfo");

    public final NumberPath<Long> exp = createNumber("exp", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath inGameRole = createString("inGameRole");

    public final NumberPath<Long> level = createNumber("level", Long.class);

    public final StringPath nickname = createString("nickname");

    public QMemberGameInfo(String variable) {
        super(MemberGameInfo.class, forVariable(variable));
    }

    public QMemberGameInfo(Path<? extends MemberGameInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberGameInfo(PathMetadata metadata) {
        super(MemberGameInfo.class, metadata);
    }

}

