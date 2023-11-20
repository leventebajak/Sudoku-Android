package com.leventebajak.sudokugenerator.dlx

/**
 * The nodes of the circular doubly-linked list in [DLX].
 *
 * @property up The node above this node.
 * @property down The node below this node.
 * @property left The node to the left of this node.
 * @property right The node to the right of this node.
 * @property column The [Column] header of this node (if any).
 */
open class Node {
    var up: Node = this
    var down: Node = this
    var left: Node = this
    var right: Node = this
    var column: Column? = null

    override fun toString(): String {
        return "Node(column=${column?.id})"
    }
}