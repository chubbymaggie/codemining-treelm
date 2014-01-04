package codemining.lm.grammar.tree;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A single AST Node Symbol
 * 
 * @author Miltiadis Allamanis <m.allamanis@ed.ac.uk>
 * 
 */
public class ASTNodeSymbol implements Serializable {

	/**
	 * String annotation containing the node.
	 */
	public static final String CURRENT_NODE_ANNOTATION = "NODE_ANNOTATION";

	/**
	 * The current node in a multi-node.
	 */
	public static final String CURRENT_NODE_MULTI_PROPERTY = "CURRENT_NODE";

	/**
	 * A Node Type to represent a continuation for binarizing child lists to
	 * rules.
	 */
	public static final int MULTI_NODE = -1;

	/**
	 * The next property of a multi-node
	 */
	public static final String NEXT_PROPERTY = "NEXT";

	private static final long serialVersionUID = -8684027920801300413L;

	public static final int TEMPLATE_NODE = -2;

	public static final int UNK_SYMBOL = Integer.MIN_VALUE;

	/**
	 * A map of annotations to their respective values. Annotations are not
	 * structural properties of the node
	 */
	private SortedMap<String, Object> annotations = Maps.newTreeMap();

	/**
	 * A list of the child properties that contain the node children
	 */
	private List<String> childProperties = Lists.newArrayList();

	/**
	 * The type of the node.
	 */
	public final int nodeType;

	/**
	 * A map of properties to their respective values
	 */
	private SortedMap<String, Object> simplePropValues = Maps.newTreeMap();

	public ASTNodeSymbol(final int type) {
		nodeType = type;
	}

	public synchronized void addAnnotation(final String annotation,
			final Object value) {
		annotations.put(checkNotNull(annotation), checkNotNull(value));
	}

	public synchronized void addChildProperty(final String propertyName) {
		checkNotNull(propertyName);
		childProperties.add(propertyName);
	}

	/**
	 * Add a simple property to the symbol.
	 * 
	 * @param propertyName
	 * @param value
	 */
	public synchronized void addSimpleProperty(final String propertyName,
			final Object value) {
		checkNotNull(propertyName);
		checkNotNull(value);
		simplePropValues.put(propertyName, value);
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ASTNodeSymbol)) {
			return false;
		}
		final ASTNodeSymbol oSymbol = (ASTNodeSymbol) other;

		if (nodeType != oSymbol.nodeType) {
			return false;
		}

		if (!simplePropValues.equals(oSymbol.simplePropValues)) {
			return false;
		}

		if (!annotations.equals(oSymbol.annotations)) {
			return false;
		}

		return childProperties.equals(oSymbol.childProperties);
	}

	public final Object getAnnotation(final String annotation) {
		return annotations.get(annotation);
	}

	public final Object getSimpleProperty(final String property) {
		return simplePropValues.get(property);
	}

	public final boolean hasAnnotation(final String annotation) {
		return annotations.containsKey(annotation);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(nodeType, simplePropValues, childProperties,
				annotations);
	}

	public final boolean hasSimpleProperty(final String property) {
		return simplePropValues.containsKey(property);
	}

	public void lockFromChanges() {
		childProperties = ImmutableList.copyOf(childProperties);
		simplePropValues = ImmutableSortedMap.copyOf(simplePropValues);
		annotations = ImmutableSortedMap.copyOf(annotations);
	}

	public final int nChildProperties() {
		return childProperties.size();
	}

	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		if (nodeType == MULTI_NODE) {
			buf.append("Type : MULTI_NODE");
		} else if (nodeType == UNK_SYMBOL) {
			buf.append("Type : UNK");
		} else if (nodeType == TEMPLATE_NODE) {
			buf.append("Type : TEMPLATE_NODE");
		} else {
			buf.append("Type : "
					+ ASTNode.nodeClassForType(nodeType).getSimpleName());
		}

		if (simplePropValues.size() > 0) {
			buf.append(" Simple Props: [");
			for (final Entry<String, Object> propEntry : simplePropValues
					.entrySet()) {
				final String prop = propEntry.getKey();
				buf.append(prop + ":" + propEntry.getValue() + ", ");
			}
			buf.append(']');
		}

		if (childProperties.size() > 0) {
			buf.append(" Child Props: [");
			for (final String prop : childProperties) {
				buf.append(prop + ", ");
			}
			buf.append(']');
		}

		if (annotations.size() > 0) {
			buf.append(" Annotations: [");
			for (final Entry<String, Object> annotation : annotations
					.entrySet()) {
				buf.append(annotation.getKey() + ":" + annotation.getValue()
						+ ", ");
			}
			buf.append(']');
		}
		return buf.toString();
	}
}