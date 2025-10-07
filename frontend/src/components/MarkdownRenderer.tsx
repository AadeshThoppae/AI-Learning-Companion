'use client'

import { useEffect, useState } from 'react'
import { MDXRemote, MDXRemoteSerializeResult } from 'next-mdx-remote'
import { serialize } from 'next-mdx-remote/serialize'

interface Props {
  content: string
}

export default function MarkdownRenderer({ content }: Props) {
  const [mdxSource, setMdxSource] = useState<MDXRemoteSerializeResult | null>(null)

  useEffect(() => {
    const serializeContent = async () => {
      try {
        const serialized = await serialize(content)
        setMdxSource(serialized)
      } catch (error) {
        console.error('Error serializing markdown:', error)
      }
    }

    serializeContent()
  }, [content])

  if (!mdxSource) {
    return <div>Loading...</div>
  }

  return <MDXRemote {...mdxSource} />
}