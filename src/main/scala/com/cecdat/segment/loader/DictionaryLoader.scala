package com.cecdat.segment.loader

import com.hankcs.hanlp.dictionary.CustomDictionary

import scala.io.Source

object DictionaryLoader {

  def apply(path: String, pos: String): Unit = {
    loadDictionary(path, pos)
  }

  private def loadDictionary(path: String, pos: String): Unit = {

    val inputStream = getClass.getClassLoader.getResourceAsStream(path)
    val source = Source.fromInputStream(inputStream)
    source.getLines().foreach(line => {
      if (!skip(line)) {
        CustomDictionary.insert(line, s"${pos} 10")
      }
    })
  }

  private def skip(word: String): Boolean = word.startsWith("#") || word.isEmpty
}
