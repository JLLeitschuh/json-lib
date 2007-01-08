/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.json;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.util.JSONTokener;
import net.sf.json.util.JSONUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A JSONArray is an ordered sequence of values. Its external text form is a
 * string wrapped in square brackets with commas separating the values. The
 * internal form is an object having <code>get</code> and <code>opt</code>
 * methods for accessing the values by index, and <code>put</code> methods for
 * adding or replacing values. The values can be any of these types:
 * <code>Boolean</code>, <code>JSONArray</code>, <code>JSONObject</code>,
 * <code>Number</code>, <code>String</code>, or the
 * <code>JSONNull object</code>.
 * <p>
 * The constructor can convert a JSON text into a Java object. The
 * <code>toString</code> method converts to JSON text.
 * <p>
 * A <code>get</code> method returns a value if one can be found, and throws
 * an exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and
 * type coersion for you.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * JSON syntax rules. The constructors are more forgiving in the texts they will
 * accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing bracket.</li>
 * <li>The <code>null</code> value will be inserted when there is
 * <code>,</code>&nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a
 * quote or single quote, and if they do not contain leading or trailing spaces,
 * and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>,
 * or <code>null</code>.</li>
 * <li>Values can be separated by <code>;</code> <small>(semicolon)</small>
 * as well as by <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0-</code> <small>(octal)</small> or
 * <code>0x-</code> <small>(hex)</small> prefix.</li>
 * <li>Comments written in the slashshlash, slashstar, and hash conventions
 * will be ignored.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 3
 */
public final class JSONArray implements JSON
{
   /**
    * Creates a JSONArray from a java array.<br>
    * The java array can be multidimensional.
    *
    * @param array A Java array
    * @throws JSONException if the array can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromArray( Object[] array )
   {
      return fromArray( array, null, false );
   }

   /**
    * Creates a JSONArray from a java array.<br>
    * The java array can be multidimensional.
    *
    * @param array A Java array
    * @param excludes A group of property names to be excluded
    * @throws JSONException if the array can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromArray( Object[] array, String[] excludes )
   {
      return fromArray( array, excludes, false );
   }

   /**
    * Creates a JSONArray from a java array.<br>
    * The java array can be multidimensional.
    *
    * @param array A Java array
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @throws JSONException if the array can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromArray( Object[] array, String[] excludes,
         boolean ignoreDefaultExcludes )
   {
      return new JSONArray( array, excludes, ignoreDefaultExcludes );
   }

   /**
    * Creates a JSONArray from a Collection.<br>
    * Its elements can be maps, POJOs, java arrays (primitive & object),
    * collections.
    *
    * @param collection A collection
    * @throws JSONException if the collection can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromCollection( Collection collection )
   {
      return fromCollection( collection, null, false );
   }

   /**
    * Creates a JSONArray from a Collection.<br>
    * Its elements can be maps, POJOs, java arrays (primitive & object),
    * collections.
    *
    * @param collection A collection
    * @param excludes A group of property names to be excluded
    * @throws JSONException if the collection can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromCollection( Collection collection, String[] excludes )
   {
      return fromCollection( collection, excludes, false );
   }

   /**
    * Creates a JSONArray from a Collection.<br>
    * Its elements can be maps, POJOs, java arrays (primitive & object),
    * collections.
    *
    * @param collection A collection
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @throws JSONException if the collection can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromCollection( Collection collection, String[] excludes,
         boolean ignoreDefaultExcludes )
   {
      return new JSONArray( collection, excludes, ignoreDefaultExcludes );
   }

   /**
    * Creates a JSONArray from a JSONString.<br>
    *
    * @param string
    * @throws JSONException if the string can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromJSONString( JSONString string )
   {
      return fromJSONString( string, null, false );
   }

   /**
    * Creates a JSONArray from a JSONString.<br>
    *
    * @param string
    * @param excludes A group of property names to be excluded
    * @throws JSONException if the string can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromJSONString( JSONString string, String[] excludes )
   {
      return fromJSONString( string, excludes, false );
   }

   /**
    * Creates a JSONArray from a JSONString.<br>
    *
    * @param string
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @throws JSONException if the string can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromJSONString( JSONString string, String[] excludes,
         boolean ignoreDefaultExcludes )
   {
      return fromJSONTokener( new JSONTokener( string.toJSONString() ), excludes,
            ignoreDefaultExcludes );
   }

   /**
    * Creates a JSONArray.<br>
    * Inspects the object type to call the correct JSONArray factory method.
    *
    * @param object
    * @throws JSONException if the object can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromObject( Object object )
   {
      return fromObject( object, null, false );
   }

   /**
    * Creates a JSONArray.<br>
    * Inspects the object type to call the correct JSONArray factory method.
    *
    * @param object
    * @param excludes A group of property names to be excluded
    * @throws JSONException if the object can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromObject( Object object, String[] excludes )
   {
      return fromObject( object, excludes, false );
   }

   /**
    * Creates a JSONArray.<br>
    * Inspects the object type to call the correct JSONArray factory method.
    *
    * @param object
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @throws JSONException if the object can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromObject( Object object, String[] excludes,
         boolean ignoreDefaultExcludes )
   {
      if( object instanceof JSONString ){
         return fromJSONString( (JSONString) object, excludes, ignoreDefaultExcludes );
      }else if( object instanceof Collection ){
         return fromCollection( (Collection) object, excludes, ignoreDefaultExcludes );
      }else if( object instanceof JSONArray ){
         return new JSONArray( (JSONArray) object );
      }else if( object instanceof JSONTokener ){
         return fromJSONTokener( (JSONTokener) object, excludes, ignoreDefaultExcludes );
      }else if( object instanceof String ){
         return fromString( (String) object, excludes, ignoreDefaultExcludes );
      }else if( object != null && object.getClass()
            .isArray() ){
         Class type = object.getClass()
               .getComponentType();
         if( !type.isPrimitive() ){
            return fromArray( (Object[]) object, excludes, ignoreDefaultExcludes );
         }else{
            if( type == Boolean.TYPE ){
               return new JSONArray( (boolean[]) object );
            }else if( type == Byte.TYPE ){
               return new JSONArray( (byte[]) object );
            }else if( type == Short.TYPE ){
               return new JSONArray( (short[]) object );
            }else if( type == Integer.TYPE ){
               return new JSONArray( (int[]) object );
            }else if( type == Long.TYPE ){
               return new JSONArray( (long[]) object );
            }else if( type == Float.TYPE ){
               return new JSONArray( (float[]) object );
            }else if( type == Double.TYPE ){
               return new JSONArray( (double[]) object );
            }else if( type == Character.TYPE ){
               return new JSONArray( (char[]) object );
            }else{
               throw new JSONException( "Unsupported type" );
            }
         }
      }else if( JSONUtils.isBoolean( object ) || JSONUtils.isFunction( object )
            || JSONUtils.isNumber( object ) || JSONUtils.isNull( object )
            || JSONUtils.isString( object ) || object instanceof JSON ){
         return new JSONArray().put( object, excludes, ignoreDefaultExcludes );
      }else if( object instanceof Enum ){
         return new JSONArray( (Enum) object );
      }else if( object instanceof Annotation || (object != null && object.getClass()
            .isAnnotation()) ){
         throw new JSONException( "Unsupported type" );
      }else if( JSONUtils.isObject( object ) ){
         return new JSONArray().put( JSONObject.fromObject( object, excludes, ignoreDefaultExcludes ) );
      }else{
         throw new JSONException( "Unsupported type" );
      }
   }

   /**
    * Constructs a JSONArray from a string in JSON format.
    *
    * @param string A string in JSON format
    * @throws JSONException if the string can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromString( String string )
   {
      return fromString( string, null, false );
   }

   /**
    * Constructs a JSONArray from a string in JSON format.
    *
    * @param string A string in JSON format
    * @param excludes A group of property names to be excluded
    * @throws JSONException if the string can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromString( String string, String[] excludes )
   {
      return fromString( string, excludes, false );
   }

   /**
    * Constructs a JSONArray from a string in JSON format.
    *
    * @param string A string in JSON format
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @throws JSONException if the string can not be converted to a proper
    *         JSONArray.
    */
   public static JSONArray fromString( String string, String[] excludes,
         boolean ignoreDefaultExcludes )
   {
      return new JSONArray( string, excludes, ignoreDefaultExcludes );
   }

   /**
    * Returns the number of dimensions suited for a java array.
    */
   public static int[] getDimensions( JSONArray jsonArray )
   {
      // short circuit for empty arrays
      if( jsonArray == null || jsonArray.isEmpty() ){
         return new int[] { 0 };
      }

      List dims = new ArrayList();
      processArrayDimensions( jsonArray, dims, 0 );
      int[] dimensions = new int[dims.size()];
      int j = 0;
      for( Iterator i = dims.iterator(); i.hasNext(); ){
         dimensions[j++] = ((Integer) i.next()).intValue();
      }
      return dimensions;
   }

   /**
    * Creates a java array from a JSONArray.
    */
   public static Object[] toArray( JSONArray jsonArray )
   {
      return toArray( jsonArray, null, null );
   }

   /**
    * Creates a java array from a JSONArray.
    */
   public static Object[] toArray( JSONArray jsonArray, Class objectClass )
   {
      return toArray( jsonArray, objectClass, null );
   }

   /**
    * Creates a java array from a JSONArray.<br>
    * Any attribute is a JSONObject and matches a key in the classMap, it will
    * be converted to that target class.<br>
    * The classMap has the following conventions:
    * <ul>
    * <li>Every key must be an String.</li>
    * <li>Every value must be a Class.</li>
    * <li>A key may be a regular expression.</li>
    * </ul>
    */
   public static Object[] toArray( JSONArray jsonArray, Class objectClass, Map classMap )
   {
      if( jsonArray.length() == 0 ){
         return new Object[0];
      }

      int[] dimensions = JSONArray.getDimensions( jsonArray );
      Object array = Array.newInstance( Object.class, dimensions );
      int size = jsonArray.length();
      for( int i = 0; i < size; i++ ){
         Object value = jsonArray.get( i );
         if( JSONUtils.isNull( value ) ){
            Array.set( array, i, null );
         }else{
            Class type = value.getClass();
            if( JSONArray.class.isAssignableFrom( type ) ){
               Array.set( array, i, toArray( (JSONArray) value, objectClass, classMap ) );
            }else if( String.class.isAssignableFrom( type )
                  || Boolean.class.isAssignableFrom( type ) || JSONUtils.isNumber( type )
                  || Character.class.isAssignableFrom( type )
                  || JSONFunction.class.isAssignableFrom( type ) ){
               Array.set( array, i, value );
            }else{
               if( objectClass != null ){
                  Array.set( array, i,
                        JSONObject.toBean( (JSONObject) value, objectClass, classMap ) );
               }else{
                  Array.set( array, i, JSONObject.toBean( (JSONObject) value ) );
               }
            }
         }
      }
      return (Object[]) array;
   }

   /**
    * Creates a List from a JSONArray.
    */
   public static List toList( JSONArray jsonArray )
   {
      return toList( jsonArray, null, null );
   }

   /**
    * Creates a List from a JSONArray.
    */
   public static List toList( JSONArray jsonArray, Class objectClass )
   {
      return toList( jsonArray, objectClass, null );
   }

   /**
    * Creates a List from a JSONArray.<br>
    * Any attribute is a JSONObject and matches a key in the classMap, it will
    * be converted to that target class.<br>
    * The classMap has the following conventions:
    * <ul>
    * <li>Every key must be an String.</li>
    * <li>Every value must be a Class.</li>
    * <li>A key may be a regular expression.</li>
    * </ul>
    */
   public static List toList( JSONArray jsonArray, Class objectClass, Map classMap )
   {
      List list = new ArrayList();
      int size = jsonArray.length();
      for( int i = 0; i < size; i++ ){
         Object value = jsonArray.get( i );
         if( JSONUtils.isNull( value ) ){
            list.add( null );
         }else{
            Class type = value.getClass();
            if( JSONArray.class.isAssignableFrom( type ) ){
               list.add( toList( (JSONArray) value, objectClass, classMap ) );
            }else if( String.class.isAssignableFrom( type )
                  || Boolean.class.isAssignableFrom( type ) || JSONUtils.isNumber( type )
                  || Character.class.isAssignableFrom( type )
                  || JSONFunction.class.isAssignableFrom( type ) ){
               list.add( value );
            }else{
               if( objectClass != null ){
                  list.add( JSONObject.toBean( (JSONObject) value, objectClass, classMap ) );
               }else{
                  list.add( JSONObject.toBean( (JSONObject) value ) );
               }
            }
         }
      }
      return list;
   }

   /**
    * Creates a JSONArray from a JSONTokener.
    *
    * @param tokener a JSONTokener
    */
   private static JSONArray fromJSONTokener( JSONTokener tokener, String[] excludes,
         boolean ignoreDefaultValues )
   {
      return new JSONArray( tokener, excludes, ignoreDefaultValues );
   }

   private static void processArrayDimensions( JSONArray jsonArray, List dims, int index )
   {
      if( dims.size() <= index ){
         dims.add( new Integer( jsonArray.length() ) );
      }else{
         int i = ((Integer) dims.get( index )).intValue();
         if( jsonArray.length() > i ){
            dims.set( index, new Integer( jsonArray.length() ) );
         }
      }
      for( Iterator i = jsonArray.iterator(); i.hasNext(); ){
         Object item = i.next();
         if( item instanceof JSONArray ){
            processArrayDimensions( (JSONArray) item, dims, index + 1 );
         }
      }
   }

   // ------------------------------------------------------

   /**
    * The List where the JSONArray's properties are kept.
    */
   private List elements;

   /**
    * Construct an empty JSONArray.
    */
   public JSONArray()
   {
      this.elements = new ArrayList();
   }

   /**
    * Construct a JSONArray from an boolean[].<br>
    *
    * @param array An boolean[] array.
    */
   private JSONArray( boolean[] array )
   {
      this.elements = new ArrayList();
      this.elements.addAll( Arrays.asList( ArrayUtils.toObject( array ) ) );
   }

   /**
    * Construct a JSONArray from an byte[].<br>
    *
    * @param array An byte[] array.
    */
   private JSONArray( byte[] array )
   {
      this.elements = new ArrayList();
      // this.elements.addAll( Arrays.asList( ArrayUtils.toObject( array ) ) );
      for( int i = 0; i < array.length; i++ ){
         Byte b = new Byte( array[i] );
         this.elements.add( JSONUtils.transformNumber( b ) );
      }
   }

   /**
    * Construct a JSONArray from an char[].<br>
    *
    * @param array An char[] array.
    */
   private JSONArray( char[] array )
   {
      this.elements = new ArrayList();
      this.elements.addAll( Arrays.asList( ArrayUtils.toObject( array ) ) );
   }

   /**
    * Construct a JSONArray from a Collection.
    *
    * @param collection A Collection.
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @throws JSONException if the collection can not be converted to a proper
    *         JSONArray.
    */
   private JSONArray( Collection collection, String[] excludes, boolean ignoreDefaultExcludes )
   {
      this.elements = new ArrayList();
      if( collection != null ){
         for( Iterator elements = collection.iterator(); elements.hasNext(); ){
            Object element = elements.next();
            add( element, excludes, ignoreDefaultExcludes );
         }
      }
   }

   /**
    * Construct a JSONArray from an double[].<br>
    *
    * @param array An double[] array.
    */
   private JSONArray( double[] array )
   {
      this.elements = new ArrayList();
      // this.elements.addAll( Arrays.asList( ArrayUtils.toObject( array ) ) );
      // addAll iterates over all values, might as well validate at the same
      // time, no?
      for( int i = 0; i < array.length; i++ ){
         Double d = new Double( array[i] );
         JSONUtils.testValidity( d );
         this.elements.add( d );
      }
   }

   /**
    * Construct a JSONArray from an Enum value.
    *
    * @param e A enum value.
    * @throws JSONException If there is a syntax error.
    */
   private JSONArray( Enum e )
   {
      this();
      if( e != null ){
         this.elements.add( e.toString() );
      }else{
         throw new JSONException( "enum value is null" );
      }
   }

   /**
    * Construct a JSONArray from an float[].<br>
    *
    * @param array An float[] array.
    */
   private JSONArray( float[] array )
   {
      this.elements = new ArrayList();
      // this.elements.addAll( Arrays.asList( ArrayUtils.toObject( array ) ) );
      // addAll iterates over all values, might as well validate at the same
      // time, no?
      for( int i = 0; i < array.length; i++ ){
         Float f = new Float( array[i] );
         JSONUtils.testValidity( f );
         this.elements.add( f );
      }
   }

   /**
    * Construct a JSONArray from an int[].<br>
    *
    * @param array An int[] array.
    */
   private JSONArray( int[] array )
   {
      this.elements = new ArrayList();
      this.elements.addAll( Arrays.asList( ArrayUtils.toObject( array ) ) );
   }

   /**
    * Construct a JSONArray from another JSONArray.<br>
    * This method will return a shallow copy of the input array.
    *
    * @param jsonArray A JSONArray.
    * @throws JSONException If there is a syntax error.
    */
   private JSONArray( JSONArray jsonArray )
   {
      this.elements = new ArrayList();
      if( jsonArray != null ){
         this.elements.addAll( jsonArray.elements );
      }
   }

   /**
    * Construct a JSONArray from a JSONTokener.
    *
    * @param x A JSONTokener
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @throws JSONException If there is a syntax error.
    */
   private JSONArray( JSONTokener x, String[] excludes, boolean ignoreDefaultExcludes )
   {
      this();
      if( x.nextClean() != '[' ){
         throw x.syntaxError( "A JSONArray text must start with '['" );
      }
      if( x.nextClean() == ']' ){
         return;
      }
      x.back();
      for( ;; ){
         if( x.nextClean() == ',' ){
            x.back();
            put( JSONNull.getInstance() );
         }else{
            x.back();
            Object v = x.nextValue( excludes, ignoreDefaultExcludes );
            if( !JSONUtils.isFunctionHeader( v ) ){
               add( v, excludes, ignoreDefaultExcludes );
            }else{
               // read params if any
               String params = JSONUtils.getFunctionParams( (String) v );
               // read function text
               int i = 0;
               StringBuffer sb = new StringBuffer();
               for( ;; ){
                  char ch = x.next();
                  if( ch == 0 ){
                     break;
                  }
                  if( ch == '{' ){
                     i++;
                  }
                  if( ch == '}' ){
                     i--;
                  }
                  sb.append( ch );
                  if( i == 0 ){
                     break;
                  }
               }
               if( i != 0 ){
                  throw x.syntaxError( "Unbalanced '{' or '}' on prop: " + v );
               }
               // trim '{' at start and '}' at end
               String text = sb.toString();
               text = text.substring( 1, text.length() - 1 )
                     .trim();
               this.elements.add( new JSONFunction( (params != null) ? StringUtils.split( params,
                     "," ) : null, text ) );
            }
         }
         switch( x.nextClean() )
         {
            case ';':
            case ',':
               if( x.nextClean() == ']' ){
                  return;
               }
               x.back();
               break;
            case ']':
               return;
            default:
               throw x.syntaxError( "Expected a ',' or ']'" );
         }
      }
   }

   /**
    * Construct a JSONArray from an long[].<br>
    *
    * @param array An long[] array.
    */
   private JSONArray( long[] array )
   {
      this.elements = new ArrayList();
      // this.elements.addAll( Arrays.asList( ArrayUtils.toObject( array ) ) );
      for( int i = 0; i < array.length; i++ ){
         Long l = new Long( array[i] );
         this.elements.add( JSONUtils.transformNumber( l ) );
      }
   }

   /**
    * Construct a JSONArray from an Object[].<br>
    * Assumes the object hierarchy is acyclical.
    *
    * @param array An Object[] array.
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @throws JSONException if the object can not be converted to a proper
    *         JSONArray.
    */
   private JSONArray( Object[] array, String[] excludes, boolean ignoreDefaultExcludes )
   {
      this.elements = new ArrayList();
      for( int i = 0; i < array.length; i++ ){
         Object element = array[i];
         add( element, excludes, ignoreDefaultExcludes );
      }
   }

   /**
    * Construct a JSONArray from an short[].<br>
    *
    * @param array An short[] array.
    */
   private JSONArray( short[] array )
   {
      this.elements = new ArrayList();
      // this.elements.addAll( Arrays.asList( ArrayUtils.toObject( array ) ) );
      for( int i = 0; i < array.length; i++ ){
         Short s = new Short( array[i] );
         this.elements.add( JSONUtils.transformNumber( s ) );
      }
   }

   /**
    * Construct a JSONArray from a source JSON text.
    *
    * @param string A string that begins with <code>[</code>&nbsp;<small>(left
    *        bracket)</small> and ends with <code>]</code>&nbsp;<small>(right
    *        bracket)</small>.
    * @throws JSONException If there is a syntax error.
    */
   private JSONArray( String string, String[] excludes, boolean ignoreDefaultExcludes )
   {
      this( new JSONTokener( string ), excludes, ignoreDefaultExcludes );
   }

   /**
    * Get the object value associated with an index.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return An object value.
    * @throws JSONException If there is no value for the index.
    */
   public Object get( int index )
   {
      Object o = opt( index );
      if( o == null ){
         throw new JSONException( "JSONArray[" + index + "] not found." );
      }
      return o;
   }

   /**
    * Get the boolean value associated with an index. The string values "true"
    * and "false" are converted to boolean.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return The truth.
    * @throws JSONException If there is no value for the index or if the value
    *         is not convertable to boolean.
    */
   public boolean getBoolean( int index )
   {
      Object o = get( index );
      if( o.equals( Boolean.FALSE )
            || (o instanceof String && ((String) o).equalsIgnoreCase( "false" )) ){
         return false;
      }else if( o.equals( Boolean.TRUE )
            || (o instanceof String && ((String) o).equalsIgnoreCase( "true" )) ){
         return true;
      }
      throw new JSONException( "JSONArray[" + index + "] is not a Boolean." );
   }

   /**
    * Get the double value associated with an index.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return The value.
    * @throws JSONException If the key is not found or if the value cannot be
    *         converted to a number.
    */
   public double getDouble( int index )
   {
      Object o = get( index );
      try{
         return o instanceof Number ? ((Number) o).doubleValue() : Double.parseDouble( (String) o );
      }
      catch( Exception e ){
         throw new JSONException( "JSONArray[" + index + "] is not a number." );
      }
   }

   /**
    * Get the int value associated with an index.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return The value.
    * @throws JSONException If the key is not found or if the value cannot be
    *         converted to a number. if the value cannot be converted to a
    *         number.
    */
   public int getInt( int index )
   {
      Object o = get( index );
      return o instanceof Number ? ((Number) o).intValue() : (int) getDouble( index );
   }

   /**
    * Get the JSONArray associated with an index.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return A JSONArray value.
    * @throws JSONException If there is no value for the index. or if the value
    *         is not a JSONArray
    */
   public JSONArray getJSONArray( int index )
   {
      Object o = get( index );
      if( o instanceof JSONArray ){
         return (JSONArray) o;
      }
      throw new JSONException( "JSONArray[" + index + "] is not a JSONArray." );
   }

   /**
    * Get the JSONObject associated with an index.
    *
    * @param index subscript
    * @return A JSONObject value.
    * @throws JSONException If there is no value for the index or if the value
    *         is not a JSONObject
    */
   public JSONObject getJSONObject( int index )
   {
      Object o = get( index );
      if( o instanceof JSONObject ){
         return (JSONObject) o;
      }
      throw new JSONException( "JSONArray[" + index + "] is not a JSONObject." );
   }

   /**
    * Get the long value associated with an index.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return The value.
    * @throws JSONException If the key is not found or if the value cannot be
    *         converted to a number.
    */
   public long getLong( int index )
   {
      Object o = get( index );
      return o instanceof Number ? ((Number) o).longValue() : (long) getDouble( index );
   }

   /**
    * Get the string associated with an index.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return A string value.
    * @throws JSONException If there is no value for the index.
    */
   public String getString( int index )
   {
      return get( index ).toString();
   }

   public boolean isArray()
   {
      return true;
   }

   public boolean isEmpty()
   {
      return this.elements.isEmpty();
   }

   /**
    * Returns an Iterator for this JSONArray
    */
   public Iterator iterator()
   {
      return this.elements.iterator();
   }

   /**
    * Make a string from the contents of this JSONArray. The
    * <code>separator</code> string is inserted between each element. Warning:
    * This method assumes that the data structure is acyclical.
    *
    * @param separator A string that will be inserted between the elements.
    * @return a string.
    * @throws JSONException If the array contains an invalid number.
    */
   public String join( String separator )
   {
      int len = length();
      StringBuffer sb = new StringBuffer();

      for( int i = 0; i < len; i += 1 ){
         if( i > 0 ){
            sb.append( separator );
         }
         sb.append( JSONUtils.valueToString( this.elements.get( i ) ) );
      }
      return sb.toString();
   }

   /**
    * Get the number of elements in the JSONArray, included nulls.
    *
    * @return The length (or size).
    */
   public int length()
   {
      return this.elements.size();
   }

   /**
    * Get the optional object value associated with an index.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return An object value, or null if there is no object at that index.
    */
   public Object opt( int index )
   {
      return (index < 0 || index >= length()) ? null : this.elements.get( index );
   }

   /**
    * Get the optional boolean value associated with an index. It returns false
    * if there is no value at that index, or if the value is not Boolean.TRUE or
    * the String "true".
    *
    * @param index The index must be between 0 and length() - 1.
    * @return The truth.
    */
   public boolean optBoolean( int index )
   {
      return optBoolean( index, false );
   }

   /**
    * Get the optional boolean value associated with an index. It returns the
    * defaultValue if there is no value at that index or if it is not a Boolean
    * or the String "true" or "false" (case insensitive).
    *
    * @param index The index must be between 0 and length() - 1.
    * @param defaultValue A boolean default.
    * @return The truth.
    */
   public boolean optBoolean( int index, boolean defaultValue )
   {
      try{
         return getBoolean( index );
      }
      catch( Exception e ){
         return defaultValue;
      }
   }

   /**
    * Get the optional double value associated with an index. NaN is returned if
    * there is no value for the index, or if the value is not a number and
    * cannot be converted to a number.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return The value.
    */
   public double optDouble( int index )
   {
      return optDouble( index, Double.NaN );
   }

   /**
    * Get the optional double value associated with an index. The defaultValue
    * is returned if there is no value for the index, or if the value is not a
    * number and cannot be converted to a number.
    *
    * @param index subscript
    * @param defaultValue The default value.
    * @return The value.
    */
   public double optDouble( int index, double defaultValue )
   {
      try{
         return getDouble( index );
      }
      catch( Exception e ){
         return defaultValue;
      }
   }

   /**
    * Get the optional int value associated with an index. Zero is returned if
    * there is no value for the index, or if the value is not a number and
    * cannot be converted to a number.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return The value.
    */
   public int optInt( int index )
   {
      return optInt( index, 0 );
   }

   /**
    * Get the optional int value associated with an index. The defaultValue is
    * returned if there is no value for the index, or if the value is not a
    * number and cannot be converted to a number.
    *
    * @param index The index must be between 0 and length() - 1.
    * @param defaultValue The default value.
    * @return The value.
    */
   public int optInt( int index, int defaultValue )
   {
      try{
         return getInt( index );
      }
      catch( Exception e ){
         return defaultValue;
      }
   }

   /**
    * Get the optional JSONArray associated with an index.
    *
    * @param index subscript
    * @return A JSONArray value, or null if the index has no value, or if the
    *         value is not a JSONArray.
    */
   public JSONArray optJSONArray( int index )
   {
      Object o = opt( index );
      return o instanceof JSONArray ? (JSONArray) o : null;
   }

   /**
    * Get the optional JSONObject associated with an index. Null is returned if
    * the key is not found, or null if the index has no value, or if the value
    * is not a JSONObject.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return A JSONObject value.
    */
   public JSONObject optJSONObject( int index )
   {
      Object o = opt( index );
      return o instanceof JSONObject ? (JSONObject) o : null;
   }

   /**
    * Get the optional long value associated with an index. Zero is returned if
    * there is no value for the index, or if the value is not a number and
    * cannot be converted to a number.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return The value.
    */
   public long optLong( int index )
   {
      return optLong( index, 0 );
   }

   /**
    * Get the optional long value associated with an index. The defaultValue is
    * returned if there is no value for the index, or if the value is not a
    * number and cannot be converted to a number.
    *
    * @param index The index must be between 0 and length() - 1.
    * @param defaultValue The default value.
    * @return The value.
    */
   public long optLong( int index, long defaultValue )
   {
      try{
         return getLong( index );
      }
      catch( Exception e ){
         return defaultValue;
      }
   }

   /**
    * Get the optional string value associated with an index. It returns an
    * empty string if there is no value at that index. If the value is not a
    * string and is not null, then it is coverted to a string.
    *
    * @param index The index must be between 0 and length() - 1.
    * @return A String value.
    */
   public String optString( int index )
   {
      return optString( index, "" );
   }

   /**
    * Get the optional string associated with an index. The defaultValue is
    * returned if the key is not found.
    *
    * @param index The index must be between 0 and length() - 1.
    * @param defaultValue The default value.
    * @return A String value.
    */
   public String optString( int index, String defaultValue )
   {
      Object o = opt( index );
      return o != null ? o.toString() : defaultValue;
   }

   /**
    * Append a boolean value. This increases the array's length by one.
    *
    * @param value A boolean value.
    * @return this.
    */
   public JSONArray put( boolean value )
   {
      put( value ? Boolean.TRUE : Boolean.FALSE );
      return this;
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONArray which is
    * produced from a Collection.
    *
    * @param value A Collection value.
    * @return this.
    */
   public JSONArray put( Collection value )
   {
      return put( value, null, false );
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONArray which is
    * produced from a Collection.
    *
    * @param value A Collection value.
    * @param excludes A group of property names to be excluded
    * @return this.
    */
   public JSONArray put( Collection value, String[] excludes )
   {
      return put( value, excludes, false );
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONArray which is
    * produced from a Collection.
    *
    * @param value A Collection value.
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @return this.
    */
   public JSONArray put( Collection value, String[] excludes, boolean ignoreDefaultExcludes )
   {
      put( fromCollection( value, excludes, ignoreDefaultExcludes ) );
      return this;
   }

   /**
    * Append a double value. This increases the array's length by one.
    *
    * @param value A double value.
    * @throws JSONException if the value is not finite.
    * @return this.
    */
   public JSONArray put( double value )
   {
      Double d = new Double( value );
      JSONUtils.testValidity( d );
      put( d );
      return this;
   }

   /**
    * Append an int value. This increases the array's length by one.
    *
    * @param value An int value.
    * @return this.
    */
   public JSONArray put( int value )
   {
      put( new Integer( value ) );
      return this;
   }

   /**
    * Put or replace a boolean value in the JSONArray. If the index is greater
    * than the length of the JSONArray, then null elements will be added as
    * necessary to pad it out.
    *
    * @param index The subscript.
    * @param value A boolean value.
    * @return this.
    * @throws JSONException If the index is negative.
    */
   public JSONArray put( int index, boolean value )
   {
      put( index, value ? Boolean.TRUE : Boolean.FALSE );
      return this;
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONArray which is
    * produced from a Collection.
    *
    * @param index The subscript.
    * @param value A Collection value.
    * @return this.
    * @throws JSONException If the index is negative or if the value is not
    *         finite.
    */
   public JSONArray put( int index, Collection value )
   {
      return put( index, value, null, false );
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONArray which is
    * produced from a Collection.
    *
    * @param index The subscript.
    * @param value A Collection value.
    * @param excludes A group of property names to be excluded
    * @return this.
    * @throws JSONException If the index is negative or if the value is not
    *         finite.
    */
   public JSONArray put( int index, Collection value, String[] excludes )
   {
      return put( index, value, excludes, false );
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONArray which is
    * produced from a Collection.
    *
    * @param index The subscript.
    * @param value A Collection value.
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @return this.
    * @throws JSONException If the index is negative or if the value is not
    *         finite.
    */
   public JSONArray put( int index, Collection value, String[] excludes,
         boolean ignoreDefaultExcludes )
   {
      put( index, fromCollection( value, excludes, ignoreDefaultExcludes ) );
      return this;
   }

   /**
    * Put or replace a double value. If the index is greater than the length of
    * the JSONArray, then null elements will be added as necessary to pad it
    * out.
    *
    * @param index The subscript.
    * @param value A double value.
    * @return this.
    * @throws JSONException If the index is negative or if the value is not
    *         finite.
    */
   public JSONArray put( int index, double value )
   {
      put( index, new Double( value ) );
      return this;
   }

   /**
    * Put or replace an int value. If the index is greater than the length of
    * the JSONArray, then null elements will be added as necessary to pad it
    * out.
    *
    * @param index The subscript.
    * @param value An int value.
    * @return this.
    * @throws JSONException If the index is negative.
    */
   public JSONArray put( int index, int value )
   {
      put( index, new Integer( value ) );
      return this;
   }

   /**
    * Put or replace a long value. If the index is greater than the length of
    * the JSONArray, then null elements will be added as necessary to pad it
    * out.
    *
    * @param index The subscript.
    * @param value A long value.
    * @return this.
    * @throws JSONException If the index is negative.
    */
   public JSONArray put( int index, long value )
   {
      put( index, new Long( value ) );
      return this;
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONObject which
    * is produced from a Map.
    *
    * @param index The subscript.
    * @param value The Map value.
    * @return this.
    * @throws JSONException If the index is negative or if the the value is an
    *         invalid number.
    */
   public JSONArray put( int index, Map value )
   {
      return put( index, value, null, false );
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONObject which
    * is produced from a Map.
    *
    * @param index The subscript.
    * @param value The Map value.
    * @param excludes A group of property names to be excluded
    * @return this.
    * @throws JSONException If the index is negative or if the the value is an
    *         invalid number.
    */
   public JSONArray put( int index, Map value, String[] excludes )
   {
      return put( index, value, excludes, false );
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONObject which
    * is produced from a Map.
    *
    * @param index The subscript.
    * @param value The Map value.
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @return this.
    * @throws JSONException If the index is negative or if the the value is an
    *         invalid number.
    */
   public JSONArray put( int index, Map value, String[] excludes, boolean ignoreDefaultExcludes )
   {
      put( index, JSONObject.fromObject( value, excludes, ignoreDefaultExcludes ) );
      return this;
   }

   /**
    * Put or replace an object value in the JSONArray. If the index is greater
    * than the length of the JSONArray, then null elements will be added as
    * necessary to pad it out.
    *
    * @param index The subscript.
    * @param value An object value. The value should be a Boolean, Double,
    *        Integer, JSONArray, JSONObject, JSONFunction, Long, String,
    *        JSONString or the JSONNull object.
    * @return this.
    * @throws JSONException If the index is negative or if the the value is an
    *         invalid number.
    */
   public JSONArray put( int index, Object value )
   {
      return put( index, value, null, false );
   }

   /**
    * Put or replace an object value in the JSONArray. If the index is greater
    * than the length of the JSONArray, then null elements will be added as
    * necessary to pad it out.
    *
    * @param index The subscript.
    * @param value An object value. The value should be a Boolean, Double,
    *        Integer, JSONArray, JSONObject, JSONFunction, Long, String,
    *        JSONString or the JSONNull object.
    * @param excludes A group of property names to be excluded
    * @return this.
    * @throws JSONException If the index is negative or if the the value is an
    *         invalid number.
    */
   public JSONArray put( int index, Object value, String[] excludes )
   {
      return put( index, value, excludes, false );
   }

   /**
    * Put or replace an object value in the JSONArray. If the index is greater
    * than the length of the JSONArray, then null elements will be added as
    * necessary to pad it out.
    *
    * @param index The subscript.
    * @param value An object value. The value should be a Boolean, Double,
    *        Integer, JSONArray, JSONObject, JSONFunction, Long, String,
    *        JSONString or the JSONNull object.
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @return this.
    * @throws JSONException If the index is negative or if the the value is an
    *         invalid number.
    */
   public JSONArray put( int index, Object value, String[] excludes, boolean ignoreDefaultExcludes )
   {
      JSONUtils.testValidity( value );
      if( index < 0 ){
         throw new JSONException( "JSONArray[" + index + "] not found." );
      }
      if( index < length() ){
         if( (value != null && Class.class.isAssignableFrom( value.getClass() ))
               || value instanceof Class ){
            this.elements.set( index, ((Class) value).getName() );
         }else if( JSONUtils.isFunction( value ) ){
            this.elements.set( index, value );
         }else if( value instanceof JSONString ){
            this.elements.set( index, JSONSerializer.toJSON( (JSONString) value, excludes,
                  ignoreDefaultExcludes ) );
         }else if( JSONUtils.isArray( value ) ){
            this.elements.set( index, JSONArray.fromObject( value, excludes, ignoreDefaultExcludes ) );
         }else if( value instanceof JSON ){
            this.elements.set( index, value );
         }else if( value instanceof JSONTokener ){
            this.elements.set( index, fromJSONTokener( (JSONTokener) value, excludes,
                  ignoreDefaultExcludes ) );
         }else if( JSONUtils.isString( value ) ){
            String str = String.valueOf( value );
            if( JSONUtils.mayBeJSON( str ) ){
               this.elements.set( index, JSONSerializer.toJSON( str, excludes,
                     ignoreDefaultExcludes ) );
            }else{
               this.elements.set( index, str );
            }
         }else if( JSONUtils.isNumber( value ) || JSONUtils.isBoolean( value ) ){
            JSONUtils.testValidity( value );
            this.elements.set( index, value );
         }else if( value instanceof Enum ){
            this.elements.set( index, String.valueOf( value ) );
         }else{
            JSONObject jsonObject = JSONObject.fromObject( value, excludes, ignoreDefaultExcludes );
            if( jsonObject.isNullObject() ){
               this.elements.set( index, JSONNull.getInstance() );
            }else{
               this.elements.set( index, jsonObject );
            }
         }
      }else{
         while( index != length() ){
            put( JSONNull.getInstance() );
         }
         put( value, excludes, ignoreDefaultExcludes );
      }
      return this;
   }

   /**
    * Put or replace a String value in the JSONArray. If the index is greater
    * than the length of the JSONArray, then null elements will be added as
    * necessary to pad it out.<br>
    * The string may be a valid JSON formatted string, in tha case, it will be
    * trabsformed to a JSONArray, JSONObjetc or JSONNull.
    *
    * @param index The subscript.
    * @param value A String value.
    * @return this.
    * @throws JSONException If the index is negative or if the the value is an
    *         invalid number.
    */
   public JSONArray put( int index, String value )
   {
      return put( index, value, null, false );
   }

   /**
    * Put or replace a String value in the JSONArray. If the index is greater
    * than the length of the JSONArray, then null elements will be added as
    * necessary to pad it out.<br>
    * The string may be a valid JSON formatted string, in tha case, it will be
    * trabsformed to a JSONArray, JSONObjetc or JSONNull.
    *
    * @param index The subscript.
    * @param value A String value.
    * @param excludes A group of property names to be excluded
    * @return this.
    * @throws JSONException If the index is negative or if the the value is an
    *         invalid number.
    */
   public JSONArray put( int index, String value, String[] excludes )
   {
      return put( index, value, excludes, false );
   }

   /**
    * Put or replace a String value in the JSONArray. If the index is greater
    * than the length of the JSONArray, then null elements will be added as
    * necessary to pad it out.<br>
    * The string may be a valid JSON formatted string, in tha case, it will be
    * trabsformed to a JSONArray, JSONObjetc or JSONNull.
    *
    * @param index The subscript.
    * @param value A String value.
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @return this.
    * @throws JSONException If the index is negative or if the the value is an
    *         invalid number.
    */
   public JSONArray put( int index, String value, String[] excludes, boolean ignoreDefaultExcludes )
   {
      if( index < 0 ){
         throw new JSONException( "JSONArray[" + index + "] not found." );
      }
      if( index < length() ){
         if( value == null ){
            this.elements.set( index, "" );
         }else if( JSONUtils.mayBeJSON( value ) ){
            this.elements.set( index,
                  JSONSerializer.toJSON( value, excludes, ignoreDefaultExcludes ) );
         }else{
            this.elements.set( index, value );
         }
      }else{
         while( index != length() ){
            put( JSONNull.getInstance() );
         }
         put( value, excludes, ignoreDefaultExcludes );
      }
      return this;
   }

   /**
    * Append an JSON value. This increases the array's length by one.
    *
    * @param value An JSON value.
    * @return this.
    */
   public JSONArray put( JSON value )
   {
      this.elements.add( value );
      return this;
   }

   /**
    * Append an long value. This increases the array's length by one.
    *
    * @param value A long value.
    * @return this.
    */
   public JSONArray put( long value )
   {
      put( JSONUtils.transformNumber( new Long( value ) ) );
      return this;
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONObject which
    * is produced from a Map.
    *
    * @param value A Map value.
    * @return this.
    */
   public JSONArray put( Map value )
   {
      return put( value, null, false );
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONObject which
    * is produced from a Map.
    *
    * @param value A Map value.
    * @param excludes A group of property names to be excluded
    * @return this.
    */
   public JSONArray put( Map value, String[] excludes )
   {
      return put( value, excludes, false );
   }

   /**
    * Put a value in the JSONArray, where the value will be a JSONObject which
    * is produced from a Map.
    *
    * @param value A Map value.
    * @param excludes A group of property names to be excluded
    * @return this.
    */
   public JSONArray put( Map value, String[] excludes, boolean ignoreDefaultExcludes )
   {
      put( JSONObject.fromObject( value, excludes, ignoreDefaultExcludes ) );
      return this;
   }

   /**
    * Append an object value. This increases the array's length by one.
    *
    * @param value An object value. The value should be a Boolean, Double,
    *        Integer, JSONArray, JSONObject, JSONFunction, Long, String,
    *        JSONString or the JSONNull object.
    * @return this.
    */
   public JSONArray put( Object value )
   {
      return put( value, null, false );
   }

   /**
    * Append an object value. This increases the array's length by one.
    *
    * @param value An object value. The value should be a Boolean, Double,
    *        Integer, JSONArray, JSONObject, JSONFunction, Long, String,
    *        JSONString or the JSONNull object.
    * @param excludes A group of property names to be excluded
    * @return this.
    */
   public JSONArray put( Object value, String[] excludes )
   {
      return put( value, excludes, false );
   }

   /**
    * Append an object value. This increases the array's length by one.
    *
    * @param value An object value. The value should be a Boolean, Double,
    *        Integer, JSONArray, JSONObject, JSONFunction, Long, String,
    *        JSONString or the JSONNull object.
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @return this.
    */
   public JSONArray put( Object value, String[] excludes, boolean ignoreDefaultExcludes )
   {
      return add( value, excludes, ignoreDefaultExcludes );
   }

   /**
    * Append a String value. This increases the array's length by one.<br>
    * The string may be a valid JSON formatted string, in tha case, it will be
    * trabsformed to a JSONArray, JSONObjetc or JSONNull.
    *
    * @param value A String value.
    * @return this.
    */
   public JSONArray put( String value )
   {
      return put( value, null, false );
   }

   /**
    * Append a String value. This increases the array's length by one.<br>
    * The string may be a valid JSON formatted string, in tha case, it will be
    * trabsformed to a JSONArray, JSONObjetc or JSONNull.
    *
    * @param value A String value.
    * @param excludes A group of property names to be excluded
    * @return this.
    */
   public JSONArray put( String value, String[] excludes )
   {
      return put( value, excludes, false );
   }

   /**
    * Append a String value. This increases the array's length by one.<br>
    * The string may be a valid JSON formatted string, in tha case, it will be
    * trabsformed to a JSONArray, JSONObjetc or JSONNull.
    *
    * @param value A String value.
    * @param excludes A group of property names to be excluded
    * @param ignoreDefaultExcludes A flag for ignoring the default exclusions of
    *        property names
    * @return this.
    */
   public JSONArray put( String value, String[] excludes, boolean ignoreDefaultExcludes )
   {
      if( value == null ){
         this.elements.add( "" );
      }else if( JSONUtils.mayBeJSON( value ) ){
         this.elements.add( JSONSerializer.toJSON( value ) );
      }else{
         this.elements.add( value );
      }
      return this;
   }

   /**
    * Produce an Object[] with the contents of this JSONArray.
    */
   public Object[] toArray()
   {
      return this.elements.toArray();
   }

   /**
    * Produce a JSONObject by combining a JSONArray of names with the values of
    * this JSONArray.
    *
    * @param names A JSONArray containing a list of key strings. These will be
    *        paired with the values.
    * @return A JSONObject, or null if there are no names or if this JSONArray
    *         has no values.
    * @throws JSONException If any of the names are null.
    */
   public JSONObject toJSONObject( JSONArray names )
   {
      if( names == null || names.length() == 0 || length() == 0 ){
         return null;
      }
      JSONObject jo = new JSONObject();
      for( int i = 0; i < names.length(); i++ ){
         jo.put( names.getString( i ), this.opt( i ) );
      }
      return jo;
   }

   /**
    * Make a JSON text of this JSONArray. For compactness, no unnecessary
    * whitespace is added. If it is not possible to produce a syntactically
    * correct JSON text then null will be returned instead. This could occur if
    * the array contains an invalid number.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @return a printable, displayable, transmittable representation of the
    *         array.
    */
   public String toString()
   {
      try{
         return '[' + join( "," ) + ']';
      }
      catch( Exception e ){
         return null;
      }
   }

   /**
    * Make a prettyprinted JSON text of this JSONArray. Warning: This method
    * assumes that the data structure is acyclical.
    *
    * @param indentFactor The number of spaces to add to each level of
    *        indentation.
    * @return a printable, displayable, transmittable representation of the
    *         object, beginning with <code>[</code>&nbsp;<small>(left
    *         bracket)</small> and ending with <code>]</code>&nbsp;<small>(right
    *         bracket)</small>.
    * @throws JSONException
    */
   public String toString( int indentFactor )
   {
      return toString( indentFactor, 0 );
   }

   /**
    * Make a prettyprinted JSON text of this JSONArray. Warning: This method
    * assumes that the data structure is acyclical.
    *
    * @param indentFactor The number of spaces to add to each level of
    *        indentation.
    * @param indent The indention of the top level.
    * @return a printable, displayable, transmittable representation of the
    *         array.
    * @throws JSONException
    */
   public String toString( int indentFactor, int indent )
   {
      int len = length();
      if( len == 0 ){
         return "[]";
      }
      int i;
      StringBuffer sb = new StringBuffer( "[" );
      if( len == 1 ){
         sb.append( JSONUtils.valueToString( this.elements.get( 0 ), indentFactor, indent ) );
      }else{
         int newindent = indent + indentFactor;
         sb.append( '\n' );
         for( i = 0; i < len; i += 1 ){
            if( i > 0 ){
               sb.append( ",\n" );
            }
            for( int j = 0; j < newindent; j += 1 ){
               sb.append( ' ' );
            }
            sb.append( JSONUtils.valueToString( this.elements.get( i ), indentFactor, newindent ) );
         }
         sb.append( '\n' );
         for( i = 0; i < indent; i += 1 ){
            sb.append( ' ' );
         }
         for( i = 0; i < indent; i += 1 ){
            sb.insert( 0, ' ' );
         }
      }
      sb.append( ']' );
      return sb.toString();
   }

   /**
    * Write the contents of the JSONArray as JSON text to a writer. For
    * compactness, no whitespace is added.
    * <p>
    * Warning: This method assumes that the data structure is acyclical.
    *
    * @return The writer.
    * @throws JSONException
    */
   public Writer write( Writer writer )
   {
      try{
         boolean b = false;
         int len = length();

         writer.write( '[' );

         for( int i = 0; i < len; i += 1 ){
            if( b ){
               writer.write( ',' );
            }
            Object v = this.elements.get( i );
            if( v instanceof JSONObject ){
               ((JSONObject) v).write( writer );
            }else if( v instanceof JSONArray ){
               ((JSONArray) v).write( writer );
            }else{
               writer.write( JSONUtils.valueToString( v ) );
            }
            b = true;
         }
         writer.write( ']' );
         return writer;
      }
      catch( IOException e ){
         throw new JSONException( e );
      }
   }

   /**
    * Append an object value. This increases the array's length by one.
    *
    * @param value An object value. The value should be a Boolean, Double,
    *        Integer, JSONArray, JSONObject, JSONFunction, Long, String,
    *        JSONString or the JSONNull object.
    * @return this.
    */
   private JSONArray add( Object value, String[] excludes, boolean ignoreDefaultValues )
   {
      if( (value != null && Class.class.isAssignableFrom( value.getClass() ))
            || value instanceof Class ){
         this.elements.add( ((Class) value).getName() );
      }else if( JSONUtils.isFunction( value ) ){
         this.elements.add( value );
      }else if( value instanceof JSONString ){
         this.elements.add( JSONSerializer.toJSON( (JSONString) value, excludes,
               ignoreDefaultValues ) );
      }else if( JSONUtils.isArray( value ) ){
         this.elements.add( JSONArray.fromObject( value, excludes, ignoreDefaultValues ) );
      }else if( value instanceof JSON ){
         this.elements.add( value );
      }else if( value instanceof JSONTokener ){
         this.elements.add( fromJSONTokener( (JSONTokener) value, excludes, ignoreDefaultValues ) );
      }else if( JSONUtils.isString( value ) ){
         String str = String.valueOf( value );
         if( JSONUtils.mayBeJSON( str ) ){
            this.elements.add( JSONSerializer.toJSON( str, excludes, ignoreDefaultValues ) );
         }else{
            this.elements.add( str );
         }
      }else if( JSONUtils.isNumber( value ) ){
         JSONUtils.testValidity( value );
         this.elements.add( JSONUtils.transformNumber( (Number) value ) );
      }else if( JSONUtils.isBoolean( value ) ){
         this.elements.add( value );
      }else if( value instanceof Enum ){
         this.elements.add( String.valueOf( value ) );
      }else if( value instanceof Annotation || (value != null && value.getClass()
            .isAnnotation()) ){
         throw new JSONException( "Unsupported type" );
      }else{
         JSONObject jsonObject = JSONObject.fromObject( value, excludes, ignoreDefaultValues );
         if( jsonObject.isNullObject() ){
            this.elements.add( JSONNull.getInstance() );
         }else{
            this.elements.add( jsonObject );
         }
      }

      return this;
   }
}
