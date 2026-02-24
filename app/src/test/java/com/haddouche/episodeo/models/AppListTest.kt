package com.haddouche.episodeo.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Tests unitarios para el modelo AppList y sus subclases (SystemList y CustomList).
 * 
 * CAJA BLANCA: Estos tests conocen la estructura interna de las clases y prueban
 * el comportamiento específico de cada tipo de lista.
 */
class AppListTest {

    @Test
    fun `SystemList creation has correct properties`() {
        // Arrange
        val id = "watching"
        val name = "Viendo"
        val seriesIds = listOf(1, 2, 3)

        // Act
        val systemList = SystemList(id, name, seriesIds)

        // Assert
        assertThat(systemList.id).isEqualTo(id)
        assertThat(systemList.name).isEqualTo(name)
        assertThat(systemList.seriesIds).containsExactly(1, 2, 3).inOrder()
    }

    @Test
    fun `CustomList creation has correct properties`() {
        // Arrange
        val id = "custom-123"
        val name = "Mis Favoritas"
        val seriesIds = listOf(100, 200)
        val ownerId = "user-456"
        val isPublic = true

        // Act
        val customList = CustomList(id, name, seriesIds, ownerId, isPublic)

        // Assert
        assertThat(customList.id).isEqualTo(id)
        assertThat(customList.name).isEqualTo(name)
        assertThat(customList.seriesIds).containsExactly(100, 200).inOrder()
        assertThat(customList.ownerId).isEqualTo(ownerId)
        assertThat(customList.isPublic).isTrue()
    }

    @Test
    fun `CustomList defaults to private when isPublic not specified`() {
        // Act
        val customList = CustomList(
            id = "custom-789",
            name = "Lista Privada",
            seriesIds = emptyList(),
            ownerId = "user-123"
        )

        // Assert
        assertThat(customList.isPublic).isFalse()
    }

    @Test
    fun `SystemList with empty series list`() {
        // Act
        val systemList = SystemList("pending", "Pendiente", emptyList())

        // Assert
        assertThat(systemList.seriesIds).isEmpty()
    }

    @Test
    fun `CustomList with empty series list`() {
        // Act
        val customList = CustomList(
            id = "empty-list",
            name = "Lista Vacía",
            seriesIds = emptyList(),
            ownerId = "user-999"
        )

        // Assert
        assertThat(customList.seriesIds).isEmpty()
    }

    @Test
    fun `SystemList equality based on data class`() {
        // Arrange
        val list1 = SystemList("watching", "Viendo", listOf(1, 2, 3))
        val list2 = SystemList("watching", "Viendo", listOf(1, 2, 3))
        val list3 = SystemList("completed", "Terminadas", listOf(1, 2, 3))

        // Assert
        assertThat(list1).isEqualTo(list2)
        assertThat(list1).isNotEqualTo(list3)
    }

    @Test
    fun `CustomList equality based on data class`() {
        // Arrange
        val list1 = CustomList("id1", "Lista", listOf(1), "user1", true)
        val list2 = CustomList("id1", "Lista", listOf(1), "user1", true)
        val list3 = CustomList("id2", "Lista", listOf(1), "user1", true)

        // Assert
        assertThat(list1).isEqualTo(list2)
        assertThat(list1).isNotEqualTo(list3)
    }

    @Test
    fun `AppList polymorphism - SystemList is instance of AppList`() {
        // Arrange
        val systemList: AppList = SystemList("watching", "Viendo", listOf(1, 2))

        // Assert
        assertThat(systemList).isInstanceOf(AppList::class.java)
        assertThat(systemList).isInstanceOf(SystemList::class.java)
    }

    @Test
    fun `AppList polymorphism - CustomList is instance of AppList`() {
        // Arrange
        val customList: AppList = CustomList("id", "Lista", listOf(1), "user1")

        // Assert
        assertThat(customList).isInstanceOf(AppList::class.java)
        assertThat(customList).isInstanceOf(CustomList::class.java)
    }

    @Test
    fun `converting CustomList to UserList preserves properties`() {
        // Arrange
        val customList = CustomList(
            id = "custom-123",
            name = "Mi Lista",
            seriesIds = listOf(1, 2, 3),
            ownerId = "user-456",
            isPublic = true
        )

        // Act
        val userList = customList.toUserList()

        // Assert
        assertThat(userList.id).isEqualTo(customList.id)
        assertThat(userList.name).isEqualTo(customList.name)
        assertThat(userList.seriesIds).isEqualTo(customList.seriesIds)
        assertThat(userList.ownerId).isEqualTo(customList.ownerId)
        assertThat(userList.isPublic).isEqualTo(customList.isPublic)
    }

    @Test
    fun `list with large number of series IDs`() {
        // Arrange
        val largeSeriesList = (1..1000).toList()

        // Act
        val systemList = SystemList("watching", "Viendo", largeSeriesList)
        val customList = CustomList("id", "Lista", largeSeriesList, "user1")

        // Assert
        assertThat(systemList.seriesIds).hasSize(1000)
        assertThat(customList.seriesIds).hasSize(1000)
        assertThat(systemList.seriesIds.first()).isEqualTo(1)
        assertThat(systemList.seriesIds.last()).isEqualTo(1000)
    }
}
