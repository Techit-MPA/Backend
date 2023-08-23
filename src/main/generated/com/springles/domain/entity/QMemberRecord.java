package com.springles.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberRecord is a Querydsl query type for MemberRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberRecord extends EntityPathBase<MemberRecord> {

    private static final long serialVersionUID = 1979987354L;

    public static final QMemberRecord memberRecord = new QMemberRecord("memberRecord");

    public final NumberPath<Long> citizenCnt = createNumber("citizenCnt", Long.class);

    public final NumberPath<Long> citizenWinCnt = createNumber("citizenWinCnt", Long.class);

    public final NumberPath<Long> doctorCnt = createNumber("doctorCnt", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> killCnt = createNumber("killCnt", Long.class);

    public final NumberPath<Long> mafiaWinCnt = createNumber("mafiaWinCnt", Long.class);

    public final NumberPath<Long> policeCnt = createNumber("policeCnt", Long.class);

    public final NumberPath<Long> saveCnt = createNumber("saveCnt", Long.class);

    public final NumberPath<Long> totalCnt = createNumber("totalCnt", Long.class);

    public final NumberPath<Long> totalTime = createNumber("totalTime", Long.class);

    public QMemberRecord(String variable) {
        super(MemberRecord.class, forVariable(variable));
    }

    public QMemberRecord(Path<? extends MemberRecord> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberRecord(PathMetadata metadata) {
        super(MemberRecord.class, metadata);
    }

}

