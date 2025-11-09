package com.langdy.assignment.repository

import com.langdy.assignment.domain.LessonStatus
import com.langdy.assignment.domain.QLesson
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class LessonRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : LessonRepositoryCustom {
    private val l = QLesson.lesson

    override fun existsBookedLessonForTeacher(
        teacherId: Long,
        startAt: LocalDateTime,
        status: LessonStatus,
    ): Boolean {
        val result =
            jpaQueryFactory
                .selectOne()
                .from(l)
                .where(
                    l.teacher.id
                        .eq(teacherId)
                        .and(l.startAt.eq(startAt))
                        .and(l.status.eq(status)),
                ).fetchFirst()
        return result != null
    }

    override fun existsBookedLessonForStudent(
        studentId: Long,
        startAt: LocalDateTime,
        status: LessonStatus,
    ): Boolean {
        val result =
            jpaQueryFactory
                .selectOne()
                .from(l)
                .where(
                    l.student.id
                        .eq(studentId)
                        .and(l.startAt.eq(startAt))
                        .and(l.status.eq(status)),
                ).fetchFirst()
        return result != null
    }
}
