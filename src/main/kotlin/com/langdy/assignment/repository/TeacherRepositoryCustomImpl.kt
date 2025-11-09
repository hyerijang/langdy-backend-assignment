package com.langdy.assignment.repository

import com.langdy.assignment.domain.LessonStatus
import com.langdy.assignment.domain.QLesson
import com.langdy.assignment.domain.QTeacher
import com.langdy.assignment.dto.projection.TeacherQueryDto
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TeacherRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : TeacherRepositoryCustom {
    override fun findAvailableTeachers(
        courseId: Long,
        startAt: LocalDateTime,
    ): List<TeacherQueryDto> {
        // 특정 코스(courseId) 와 특정 시각에 대해, 수업이 비어 있는 선생님 목록을 조회
        val t = QTeacher.teacher
        val l = QLesson.lesson

        return jpaQueryFactory
            .select(Projections.constructor(TeacherQueryDto::class.java, t.id, t.name))
            .from(t)
            .leftJoin(l)
            .on(
                l.teacher.id
                    .eq(t.id)
                    .and(l.startAt.eq(startAt))
                    .and(l.status.eq(LessonStatus.BOOKED)),
            ).where(l.id.isNull())
            .fetch()
    }
}
