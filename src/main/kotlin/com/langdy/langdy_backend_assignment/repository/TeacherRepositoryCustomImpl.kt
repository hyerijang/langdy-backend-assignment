package com.langdy.langdy_backend_assignment.repository

import com.langdy.langdy_backend_assignment.domain.*
import com.langdy.langdy_backend_assignment.dto.TeacherDto
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TeacherRepositoryCustomImpl(private val jpaQueryFactory: JPAQueryFactory) : TeacherRepositoryCustom {
    override fun findAvailableTeachers(courseId: Long, startAt: LocalDateTime): List<TeacherDto> {
        // 특정 코스(courseId) 와 특정 시각에 대해, 수업이 비어 있는 선생님 목록을 조회
        val t = QTeacher.teacher
        val l = QLesson.lesson

        return jpaQueryFactory
            .select(Projections.constructor(TeacherDto::class.java, t.id, t.name))
            .from(t)
            .leftJoin(l).on(
                l.teacher.id.eq(t.id)
                    .and(l.startAt.eq(startAt))
                    .and(l.status.eq(LessonStatus.BOOKED))
            )
            .where(l.id.isNull())
            .fetch()
    }
}

